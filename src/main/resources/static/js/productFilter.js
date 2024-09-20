window.dataContainer
window.contextPath
document.addEventListener('DOMContentLoaded', () => {
    let currentOpenMenu = null;
	dataContainer = document.getElementById('data-container');
	contextPath = dataContainer.getAttribute('data-my-variable');
	contextPath = contextPath === null ? "" : contextPath;
	
	console.log(contextPath)
	
    // 상위 컨테이너에 이벤트 리스너 등록
    document.body.addEventListener('click', (event) => {
        // 클릭된 요소가 드롭다운 버튼인지 확인
        if (event.target.closest('[id^="cardmenu-button"]')) {
            event.stopPropagation();
            const button = event.target.closest('[id^="cardmenu-button"]');
            const buttonId = button.id;
            const productId = buttonId.split('cardmenu-button')[1].split('cate')[0];
            const categoryId = buttonId.split('cate')[1];
            const dropdownMenu = document.querySelector(`#dropdown-menu${productId}cate${categoryId}`);

            // 현재 열린 메뉴가 클릭된 메뉴와 다르면 닫기
            if (currentOpenMenu && currentOpenMenu !== dropdownMenu) {
                currentOpenMenu.classList.add('hidden');
            }

            // 클릭된 메뉴 토글
            if (dropdownMenu.classList.contains('hidden')) {
                dropdownMenu.classList.remove('hidden');
                currentOpenMenu = dropdownMenu;
            } else {
                dropdownMenu.classList.add('hidden');
                currentOpenMenu = null;
            }
        } else {
            // 메뉴 외부 클릭 시 모든 메뉴 닫기
            if (currentOpenMenu) {
                currentOpenMenu.classList.add('hidden');
                currentOpenMenu = null;
            }
        }
    });
});

function changeState(categoryId, productId) {
    const productState = document.getElementById(`productState-${categoryId}cate${productId}`);
    const productStatechange = document.getElementById(`productStatechange-${categoryId}cate${productId}`);
    const url = `${contextPath}/wishlist/changeState/${categoryId}/${productId}`;

    productState.innerHTML = "";
    productStatechange.innerHTML = "";

    fetch(url, {
        method: "GET"
    })
    .then(response => response.json())
    .then(product => {
        console.log(`정보 전달 성공 : ${product.state}`);

        if (product.state == "private") {
            productState.innerHTML = `
                <span class="private-label">비공개</span>
            `;
            productStatechange.innerHTML = `
                <a class="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem" tabindex="-1" id="menu-item-toPublic${product.id}cate${categoryId}" onclick="changeState(${categoryId}, ${productId})">공개로 전환</a>
                <a class="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem" tabindex="-1" onclick="deleteProduct(${product.id})">삭제</a>
            `;
        } else {
            productState.innerHTML = `
                <span class="public-label">공개</span>
            `;
            productStatechange.innerHTML = `
                <a class="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem" tabindex="-1" id="menu-item-toPrivate${product.id}cate${categoryId}" onclick="changeState(${categoryId}, ${productId})">비공개로 전환</a>
                <a class="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem" tabindex="-1" onclick="deleteProduct(${product.id})">삭제</a>
            `;
        }
    });
}

function categorySelectFilter(wishlistId, categoryId) {
    const categoryContainer = document.getElementById(`products-${categoryId}`);
    const selectElement = document.getElementById(`select_filter-cate-${categoryId}`);
    const selectValue = selectElement.value;
    const url = `${contextPath}/wishlist/category/${categoryId}/${wishlistId}/change/${selectValue}`;
    categoryContainer.innerHTML = "";
    console.log(`연결확인 : ${selectValue}`);

    fetch(url, {
        method: "GET"
    })
    .then(response => response.json())
    .then(products => {
        console.log(`${JSON.stringify(products)}`);

        products.forEach(product => {
            const productItem = document.createElement("div");
            productItem.className = "product-item p-4 border rounded-lg border-gray-300 bg-white shadow flex flex-col";
            productItem.id = `product-${product.id}`;
            productItem.innerHTML = `

                <div id="productState-${categoryId}cate${product.id}" class="flex justify-end mb-2">
                    <span class="${product.state === 'public' ? 'public-label' : 'private-label'}">${product.state === 'public' ? '공개' : '비공개'}</span>
                </div>
                
                <img src="${product.image}" alt="Product Image" class="mb-2 w-full h-40 object-cover rounded" />
                <h4 class="text-lg font-semibold mb-1">${product.title.length > 30 ? product.title.substring(0, 27) + '설정' : product.title}</h4>
                <span class="text-gray-500 mb-2">${new Intl.NumberFormat().format(product.lprice)} 원</span>                

                
                <div class="flex justify-end mb-4 relative inline-block text-left mt-2 dropdown-button">

                        <button id="cardmenu-button${product.id}cate${categoryId}" class="inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500" aria-expanded="true" aria-haspopup="true">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 12h.01M12 12h.01M18 12h.01"></path>
                            </svg>
                        </button>
                    
                    <div id="dropdown-menu${product.id}cate${categoryId}" class="hidden origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none" role="menu" aria-orientation="vertical" aria-labelledby="menu-button" tabindex="-1">
                        <div id="productStatechange-${categoryId}cate${product.id}" class="py-1" role="none">
                            <a class="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem" tabindex="-1" id="menu-item-to${product.state === 'public' ? 'Private' : 'Public'}${product.id}cate${categoryId}" onclick="changeState(${categoryId}, ${product.id})">${product.state === 'public' ? '비공개로 전환' : '공개로 전환'}</a>
                            <a class="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem" tabindex="-1" onclick="deleteProduct(${product.id})">삭제</a>
                        </div>
                    </div>
                </div>
            `;
            categoryContainer.appendChild(productItem);
        });
    });
}
