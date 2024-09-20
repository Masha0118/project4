package com.mysite.sbb.wishlist;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryRepository;
import com.mysite.sbb.mycategory.MyCategoryService;
import com.mysite.sbb.myproduct.Product;
import com.mysite.sbb.myproduct.ProductService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {

	private final WishlistService wishlistService;
	private final MyCategoryService myCategoryService;
	private final UserService userService;
	private final ProductService productService;
	private final MyCategoryRepository categoryRepository;

	
	 @Value("${server.servlet.context-path:}") private String contextPath;


	@GetMapping("/{userId}")
	public String getUserWishlists(@PathVariable("userId") Long id, Model model, Principal principal) {
		if (principal != null) {
			SiteUser user = userService.getUserById(id);
			SiteUser currentUser = userService.getUser(principal.getName());
			Wishlist wishlists = wishlistService.getWishlistsByUser(user);
			List<MyCategory> categories = myCategoryService.getMyCategorysById(wishlists.getId());

			boolean isOwnerOrAdmin = user.getUsername().equals(currentUser.getUsername())
					|| "admin".equals(currentUser.getUsername());

			// 카테고리 필터링: 비공개 카테고리는 소유자 또는 관리자만 볼 수 있도록 필터링
			List<MyCategory> visibleCategories = categories.stream()
					.filter(category -> {
						// 비공개 카테고리인 경우 소유자나 관리자가 아닌 사용자는 볼 수 없음
						if (category.getState().equals("private")) {
							return isOwnerOrAdmin;
						}
						return true; // 공개된 카테고리는 모두에게 보임
					})
					.collect(Collectors.toList());

			// 각 카테고리 내의 제품도 필터링
			visibleCategories.forEach(category -> {
				List<Product> filteredProducts = category.getProducts().stream().filter(product -> {
					// 비공개 제품은 소유자 또는 관리자만 볼 수 있음
					if (product.getState().equals("private")) {
						return isOwnerOrAdmin;
					}
					return true; // 공개 제품은 모두에게 보임
				}).collect(Collectors.toList());
				category.setProducts(filteredProducts);
			});

//			System.out.println("contextPath : " + contextPath);
			model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
			model.addAttribute("wishlists", wishlists);
			model.addAttribute("categories", visibleCategories);
			model.addAttribute("isOwner", user.getUsername().equals(currentUser.getUsername()));
			model.addAttribute("userid", user.getId());
			model.addAttribute("username", user.getId());
			model.addAttribute("principal", principal);
			model.addAttribute("userId", id);
			model.addAttribute("userRealName",user.getUsername());
			return "wishlist";
		} else {
			return "redirect:/user/login";
		}
	}


	// 모달창에 상품 정보 전달
	@GetMapping("/{userId}/categories")
	@ResponseBody
	public List<MyCategory> getCategories(@PathVariable("userId") Long userId) {
		return wishlistService.getCategories(userId);
	}

//================================================================
	@PostMapping("/{wishlistId}/categories")
	@ResponseBody
	public MyCategory addCategory(@PathVariable("wishlistId") Long wishlistId, @RequestBody MyCategory category,Principal principal) {
		Wishlist wishlist = wishlistService.getWishlistById(wishlistId);
		MyCategory mycategory = wishlistService.addCategoryToWishlist(wishlistId, category);
		mycategory.setWishlist(wishlist);
		mycategory.setUsername(principal.getName());
		categoryRepository.save(mycategory);
//		System.out.println(mycategory.getWishlist());


		// 변경된 Wishlist 저장
		Wishlist resultWishlist = wishlistService.saveWishlist(wishlist);

//		System.out.println("카테고리 추가확인: " + resultWishlist.getMycategories());

		return mycategory;
	}

//================================================================
	@DeleteMapping("/{wishlistId}/categories/{categoryId}")
	@ResponseBody
	public ResponseEntity<?> deleteCategory(@PathVariable("wishlistId") Long wishlistId,
			@PathVariable("categoryId") Long categoryId) {
		try {
			List<Product> movedProducts = wishlistService.removeCategoryFromWishlist(wishlistId, categoryId);
			return ResponseEntity.ok(movedProducts);
		} catch (RuntimeException e) {
			System.err.println("RuntimeException occurred while deleting category: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			System.err.println("Exception occurred while deleting category: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while deleting the category");
		}
	}

	@DeleteMapping("/product/{productId}")
	@ResponseBody
	public ResponseEntity<?> deleteProduct(@PathVariable("productId") Long productId) {
		try {
			wishlistService.deleteProduct(productId);
			return ResponseEntity.ok("Product deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while deleting the product");
		}
	}

	@PostMapping("/category/{categoryId}/products")
	@ResponseBody
	public ResponseEntity<Product> addProductToCategory(@PathVariable("categoryId") Long categoryId,
			@RequestBody Product product) {
		try {

			Product savedProduct = wishlistService.addProductToCategory(categoryId, product);
			return ResponseEntity.ok(savedProduct);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping("/category/{categoryId}/products/check-duplicate")
	@ResponseBody
	public ResponseEntity<String> checkDuplicateProduct(@PathVariable("categoryId") Long categoryId,
			@RequestBody Product product) {
		try {
			if (product == null || product.getProductId() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid product data");
			}
			boolean exists = wishlistService.isProductInCategory(categoryId, product.getProductId());
			if (exists) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("{\"message\": \"Product already exists in this category.\"}");
			} else {
				return ResponseEntity.ok("{\"message\": \"Product does not exist in this category.\"}");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Server error\"}");
		}
	}

	@GetMapping("/changeState/{categoryId}/{productId}")
	public ResponseEntity<Product> changeState(@PathVariable("categoryId") Long categoryId,
			@PathVariable("productId") Long productId) {

		Product product = productService.changefindByCategory_idAndId(categoryId, productId);

		return ResponseEntity.ok(product);
	}

	@GetMapping("/category/{categoryId}/{wishlistId}/change/{selectValue}")
	public ResponseEntity<List<Product>> productSelectFilter(@PathVariable("categoryId") Long categoryId,
			@PathVariable("wishlistId") Long wishlistId, @PathVariable("selectValue") String selectValue) {

		List<Product> products = productService.getProductsByCategoryId(categoryId);
		List<Product> filteredProducts;

		switch (selectValue.toLowerCase()) {
		case "public":
			filteredProducts = products.stream().filter(product -> "public".equalsIgnoreCase(product.getState()))
					.collect(Collectors.toList());
			break;
		case "private":
			filteredProducts = products.stream().filter(product -> "private".equalsIgnoreCase(product.getState()))
					.collect(Collectors.toList());
			break;
		case "all":
		default:
			filteredProducts = products; // 모든 제품을 반환
			break;
		}

//		System.out.println(filteredProducts);

		return ResponseEntity.ok(filteredProducts);
	}

	@GetMapping("/changeState/{categoryId}")
	public ResponseEntity<MyCategory> changCategoryState(@PathVariable("categoryId") Long categoryId) {

		MyCategory category = wishlistService.getCategoryById(categoryId);

		if (category.getState().equals("private")) {
			category.setState("public");
			myCategoryService.saveCategory(category);
//			System.out.println("private 에서 public로: " + category.getState());
		} else if (category.getState().equals("public")) {
			category.setState("private");
			myCategoryService.saveCategory(category);
//			System.out.println("public 에서 private로: " + category.getState());
		}

		return ResponseEntity.ok(category);
	}

}
