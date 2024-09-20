
function loadCategoriesAndProducts() {
    const userId = document.getElementById('userId').value; // 사용자 아이디 가져오기

    fetch(`${contextPath}/wishlist/${userId}/categories`)
        .then(response => response.json())
        .then(categories => {
            const categoryProductList = document.getElementById('categoryProductList');
            categoryProductList.innerHTML = '';

            categories.forEach(category => {
                const categoryDiv = document.createElement('div');
                categoryDiv.className = 'category border p-4 rounded-lg mb-4';

                const categoryTitle = document.createElement('h4');
                categoryTitle.className = 'text-xl font-bold mb-2 flex items-center'; // flex와 items-center 클래스를 추가하여 수평 정렬
                categoryTitle.textContent = category.name; // 텍스트 콘텐츠로 설정

                // 상품이 있는 경우에만 체크박스와 텍스트 추가
                if (category.products.length > 0) {
                    const label = document.createElement('label');
                    label.className = 'flex items-center ml-4';
                    label.innerHTML = `<span class="mr-2">전체 선택</span><input type="checkbox" class="form-checkbox category-checkbox" data-category-id="${category.id}">`;
                    categoryTitle.appendChild(label);
                }

                categoryDiv.appendChild(categoryTitle);

                const productList = document.createElement('ul'); // ul 태그로 상품 리스트 생성
                productList.className = 'space-y-2';
                category.products.forEach(product => {
                    const productItem = document.createElement('li');
                    productItem.id = `product-${product.id}`; // 고유 ID 설정
                    productItem.className = 'product-item border p-4 rounded-lg bg-gray-100 flex justify-between items-center';

                    const productInfo = document.createElement('div');
                    productInfo.className = 'product-info';

                    const productImage = document.createElement('img');
                    productImage.src = product.image;
                    productImage.id = 'miniImage';
                    productImage.className = 'w-16 h-16 object-cover rounded mr-4';

                    const productTitle = document.createElement('span');
                    productTitle.className = 'product-title font-bold';
                    productTitle.textContent = truncateText(product.title, 25);

                    const productPrice = document.createElement('span');
                    productPrice.className = 'product-price text-gray-500 ml-2';
                    productPrice.textContent = product.lprice.toLocaleString() + ' 원';

                    productInfo.appendChild(productImage);
                    productInfo.appendChild(productTitle);
                    productInfo.appendChild(productPrice);

                    const productMeta = document.createElement('div');
                    productMeta.className = 'product-meta flex items-center';

                    const productState = document.createElement('span');
                    productState.className = `${product.state === 'public' ? 'public-label' : 'private-label'}`;
                    productState.textContent = product.state === 'public' ? '공개' : '비공개';

                    const shareCheckbox = document.createElement('label');
                    shareCheckbox.className = 'share-checkbox flex items-center ml-2';
                    shareCheckbox.innerHTML = `<input type="checkbox" class="mr-2 product-checkbox" data-category-id="${category.id}"> 선택`;

                    const shareButton = document.createElement('button');
                    shareButton.className = 'share-button bg-blue-500 text-white px-4 py-2 rounded ml-2';
                    shareButton.textContent = '개별공유';
                    shareButton.addEventListener('click', () => openIndividualShareModal(product.id, product.image, product.title));

                    productMeta.appendChild(productState);
                    productMeta.appendChild(shareCheckbox);
                    productMeta.appendChild(shareButton);

                    productItem.appendChild(productInfo);
                    productItem.appendChild(productMeta);
                    productList.appendChild(productItem);
                });

                categoryDiv.appendChild(productList);
                categoryProductList.appendChild(categoryDiv);
            });

            // 리스트 추가 체크박스와 공유여부 체크박스 연결
            document.querySelectorAll('.category-checkbox').forEach(categoryCheckbox => {
                categoryCheckbox.addEventListener('change', function() {
                    const categoryId = this.dataset.categoryId;
                    const isChecked = this.checked;
                    document.querySelectorAll(`.product-checkbox[data-category-id="${categoryId}"]`).forEach(productCheckbox => {
                        productCheckbox.checked = isChecked;
                    });
                });
            });

            // 개별 상품 체크박스 변경 시 리스트 추가 체크박스 상태 업데이트
            document.querySelectorAll('.product-checkbox').forEach(productCheckbox => {
                productCheckbox.addEventListener('change', function() {
                    const categoryId = this.dataset.categoryId;
                    const categoryCheckbox = document.querySelector(`.category-checkbox[data-category-id="${categoryId}"]`);
                    const allChecked = document.querySelectorAll(`.product-checkbox[data-category-id="${categoryId}"]:not(:checked)`).length === 0;
                    categoryCheckbox.checked = allChecked;
                });
            });

        })
        .catch(error => {
            console.error('Error loading categories and products:', error);
        });
}

// 텍스트 길이 제한 함수
function truncateText(text, maxLength) {
    const tempElement = document.createElement('div');
    tempElement.innerHTML = text;
    const plainText = tempElement.textContent || tempElement.innerText || '';
    if (plainText.length > maxLength) {
        return plainText.slice(0, maxLength) + '...';
    }
    return plainText;
}

// 링크 생성
function generateLink() {
    const userId = document.getElementById('userId').value;
    const link = `${window.location.origin}${contextPath}/wishlist/${userId}`;
    alert(`링크가 생성되었습니다: ${link}`);
}

// 카테고리 추가 함수
function addCategory(wishlistId) {
    const categoryNameInput = document.querySelector(`#wishlist-${wishlistId} input`);
	
	
    const categoryName = categoryNameInput.value;

    if (categoryName) {
        fetch(`${contextPath}/wishlist/${wishlistId}/categories`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8'
            },
            body: JSON.stringify({ name: categoryName })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(newCategory => {
                appendCategoryToWishlist(wishlistId, newCategory);
                categoryNameInput.value = '';  // Clear input
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error adding category: ' + error.message);
            });
    }
}

// 카테고리에 새로운 카테고리 추가
function appendCategoryToWishlist(wishlistId, category) {
    const categoriesDiv = document.getElementById(`categories-${wishlistId}`);
    const categoryDiv = document.createElement('div');
    categoryDiv.id = `category-${category.id}`;
    categoryDiv.className = 'border p-4 rounded-lg bg-gray-50 shadow category';

    categoryDiv.innerHTML = `
        <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold mb-2">${category.name}</h3>
            <form class="max-w-sm mx-auto ml-2">
                <select id="select_filter-cate-${category.id}" class="bg-gray-50 border border-gray-300
                        text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600
                        dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                    onchange="categorySelectFilter(${wishlistId}, ${category.id})">
                    <option value="all" class="font-medium text-gray-900 dark:text-white" selected>전체</option>
                    <option value="public" class="font-medium text-gray-900 dark:text-white">공개</option>
                    <option value="private" class="font-medium text-gray-900 dark:text-white">비공개</option>
                </select>
            </form>
            <div id="categoryStateBtnContainer-${category.id}">
                <button id="categoryStateBtn-${category.id}" class="bg-gray-400 text-white px-4 py-2 rounded" onclick="changeCategoryState(${category.id})">${category.state === 'public' ? '공개' : '비공개'}</button>
            </div>
            <button class="delete-button bg-red-500 text-white px-4 py-2 rounded" onclick="deleteCategory(${wishlistId}, ${category.id})">카테고리 삭제</button>
        </div>
        <div class="horizontal-scroll" id="products-${category.id}">
            <!-- 카테고리에 담긴 상품을 보여주는 부분 -->
        </div>`;

    categoriesDiv.appendChild(categoryDiv);
}

// 카테고리 삭제 함수
function deleteCategory(wishlistId, categoryId) {
    if (confirm('카테고리를 삭제하시겠습니까?')) {
        fetch(`${contextPath}/wishlist/${wishlistId}/categories/${categoryId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(() => {
                alert('카테고리가 삭제되었습니다.');

                // 삭제된 카테고리의 DOM 요소 제거
                const categoryElement = document.getElementById(`category-${categoryId}`);
                if (categoryElement) {
                    categoryElement.remove();
                }

                // 기본 카테고리로 상품이 이동된 내용을 즉시 반영
                updateBasicCategoryProducts(wishlistId);
            })
            .catch(error => {
                console.error('Error deleting category:', error);
                alert('카테고리 삭제 중 오류가 발생했습니다.');
            });
    }
}

// 기본 카테고리에 새로운 상품을 추가하는 함수
function updateBasicCategoryProducts(wishlistId) {
    fetch(`${contextPath}/wishlist/${wishlistId}/categories`)
        .then(response => response.json())
        .then(categories => {
            const basicCategory = categories.find(cat => cat.name === '기본 카테고리');
            if (basicCategory) {
                const basicCategoryProducts = document.getElementById(`products-${basicCategory.id}`);

                // 기존 상품들을 유지하면서 새로 추가된 상품들만 추가
                basicCategory.products.forEach(product => {
                    if (!document.getElementById(`product-${product.id}`)) {
                        const productItem = createProductItem(product, basicCategory.id);
                        basicCategoryProducts.appendChild(productItem);
                    }
                });
            }
        })
        .then(() => {
            initializeDropdowns();  // 드롭다운 초기화
        })
        .catch(error => {
            console.error('Error updating basic category products:', error);
        });
}

// 새로운 상품 요소를 생성하는 함수
function createProductItem(product, categoryId) {
    const productItem = document.createElement('div');
    productItem.className = 'product-item p-4 border rounded-lg border-gray-300 bg-white shadow flex flex-col';
    productItem.id = `product-${product.id}`;
    productItem.innerHTML = `
        <div class="flex justify-end mb-2" id="productState-${categoryId}cate${product.id}">
            ${product.state === 'public' ?
        `<span class="public-label">공개</span>` :
        `<span class="private-label">비공개</span>`
    }
        </div>
        <img src="${product.image}" alt="Product Image" class="mb-2 w-full h-40 object-cover rounded" />
        <h4 class="text-lg font-semibold mb-1">${truncateText(product.title, 30)}</h4>
        <span class="text-gray-500 mb-2">${product.lprice.toLocaleString()} 원</span>

        <div class="flex justify-end mb-4 relative inline-block text-left mt-2 dropdown-button">
            <button class="inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500" aria-expanded="true" aria-haspopup="true">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 12h.01M12 12h.01M18 12h.01"></path>
                </svg>
            </button>
            <div class="dropdown-menu origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none" role="menu" aria-orientation="vertical" aria-labelledby="menu-button" tabindex="-1">
                <div class="py-1" role="none" id="productStatechange-${categoryId}cate${product.id}">
                    <a class="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem" tabindex="-1" onclick="changeState(${categoryId}, ${product.id})">
                        ${product.state === 'public' ? '비공개로 전환' : '공개로 전환'}
                    </a>
                    <a class="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem" tabindex="-1" onclick="deleteProduct(${product.id})">삭제</a>
                </div>
            </div>
        </div>
    `;
    return productItem;
}

// 드롭다운 메뉴 초기화 함수
function initializeDropdowns() {
    $('.dropdown-button button').off('click').on('click', function (event) {
        event.stopPropagation();
        var $dropdownMenu = $(this).parent().find('.dropdown-menu');
        if ($dropdownMenu.is(':visible')) {
            $dropdownMenu.hide();
        } else {
            $('.dropdown-menu').hide();
            $dropdownMenu.toggle();
        }
    });
    $(document).off('click').on('click', function () {
        $('.dropdown-menu').hide();
    });
}

// 상품 삭제 함수
function deleteProduct(productId) {
    if (confirm('상품을 삭제하시겠습니까?')) {
        fetch(`${contextPath}/wishlist/product/${productId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(() => {
                alert('상품이 삭제되었습니다.');
                const productElement = document.getElementById(`product-${productId}`);
                if (productElement) {
                    productElement.remove();
                } else {
                    console.error(`Element with ID product-${productId} not found.`);
                }
            })
            .catch(error => {
                console.error('Error deleting product:', error);
                alert('상품 삭제 중 오류가 발생했습니다.');
            });
    }
}

// 상품 필터링 업데이트 함수
function updateProductList(products, categoryId) {
    const productContainer = document.getElementById(`products-${categoryId}`);
    productContainer.innerHTML = ''; // 기존 내용 지우기

    products.forEach(product => {
        const productItem = document.createElement('div');
        productItem.className = 'product-item p-4 border rounded-lg border-gray-300 bg-white shadow flex flex-col';
        productItem.id = `product-${product.id}`; // 고유 ID 설정
        productItem.innerHTML = `
            <div class="flex justify-end mb-4">
                ${product.state === 'public' ?
            `<span class="pl-2 pr-2 text-gray-600 mb-10 text-l font-extrabold text-gray-900 dark:text-white p-2">공개</span>` :
            `<span class="pl-2 pr-2 text-gray-600 bg-gray-200 mb-4 text-l font-extrabold text-gray-900 dark:text-white border rounded-lg p-2 border">비공개</span>`
        }
            </div>
            <img src="${product.image}" alt="Product Image" class="mb-4 w-full h-64 object-cover rounded" />
            <h4 class="text-lg font-semibold mb-2">${truncateText(product.title, 30)}</h4>
            <span class="text-gray-500 mb-4">${product.lprice.toLocaleString()} 원</span>
            <button class="delete-button bg-red-500 text-white px-4 py-2 rounded mt-2" onclick="deleteProduct(${product.id})">삭제</button>
            <button class="bg-blue-500 text-white px-4 py-2 rounded" >개별공유</button>
        `;
        productContainer.appendChild(productItem);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const username = document.getElementById('userRealName').value; // 사용자 이름 가져오기
    document.getElementById('modalUserId').innerText = `${username} 님의 위시리스트`; // 사용자 이름 설정

    const selectElement = document.getElementById('select_filter');

    selectElement.addEventListener('change', async function () {
        const selectedValue = selectElement.value; // 선택된 옵션의 값 가져오기

        const categoryId = document.querySelector('[id^="category-"]').id.split('-')[1]; // 카테고리 ID 추출
        const wishlistId = document.querySelector('[id^="wishlist-"]').id.split('-')[1]; // 위시리스트 ID 추출
        const baseUrl = `${contextPath}/wishlist/${wishlistId}/${categoryId}/productfilter`;
        const newUrl = `${baseUrl}?filter=${encodeURIComponent(selectedValue)}`;

        try {
            const response = await fetch(newUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json(); // JSON 응답 파싱
            updateProductList(data.products, categoryId); // 화면 업데이트 함수 호출

        } catch (error) {
            console.error('Error fetching data:', error);
        }
    });

    // 리스트 추가 체크박스와 공유여부 체크박스 연결
    document.querySelectorAll('.category-checkbox').forEach(categoryCheckbox => {
        categoryCheckbox.addEventListener('change', function () {
            const categoryId = this.dataset.categoryId;
            const isChecked = this.checked;
            document.querySelectorAll(`.product-checkbox[data-category-id="${categoryId}"]`).forEach(productCheckbox => {
                productCheckbox.checked = isChecked;
            });
        });
    });

    document.querySelectorAll('.product-checkbox').forEach(productCheckbox => {
        productCheckbox.addEventListener('change', function () {
            const categoryId = this.dataset.categoryId;
            const categoryCheckbox = document.querySelector(`.category-checkbox[data-category-id="${categoryId}"]`);
            const allChecked = document.querySelectorAll(`.product-checkbox[data-category-id="${categoryId}"]:not(:checked)`).length === 0;
            categoryCheckbox.checked = allChecked;
        });
    });

});

function changeCategoryState(categoryId) {
    const categoryStateBtnContainer = document.getElementById(`categoryStateBtnContainer-${categoryId}`);
    categoryStateBtnContainer.innerHTML = "";

    const url = `${contextPath}/wishlist/changeState/${categoryId}`;
    fetch(url, {
        method: "GET"
    })
        .then(response => response.json())
        .then(category => {
            if (category.state === "private") {
                categoryStateBtnContainer.innerHTML = `
                <button id="categoryStateBtn-${category.id}" class="bg-gray-400 text-white px-4 py-2 rounded" onclick="changeCategoryState(${category.id})">비공개</button>
            `;
            } else if (category.state === "public") {
                categoryStateBtnContainer.innerHTML = `
                <button id="categoryStateBtn-${category.id}" class="bg-pink-400 text-white px-4 py-2 rounded" onclick="changeCategoryState(${category.id})">공개</button>
            `;
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });

}