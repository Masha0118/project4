let contextPath;
let itemToDeleteId = null;
let itemsToDelete = [];

document.addEventListener('DOMContentLoaded', () => {
    const dataContainer = document.getElementById('data-container');
    contextPath = dataContainer.getAttribute('data-my-variable') || "";
    contextPath = contextPath === null ? "" : contextPath;

    // 장바구니 로드
    loadCartItems();

    // 모달 버튼 이벤트 핸들러
    document.getElementById('cancel-delete-btn').addEventListener('click', closeDeleteConfirmModal);
    document.getElementById('confirm-delete-btn').addEventListener('click', confirmDelete);
    document.getElementById('close-success-modal-btn').addEventListener('click', closeSuccessModal);

    // 전체선택 체크박스 이벤트 핸들러
    $('#select-all-checkbox').change(function () {
        const isChecked = $(this).is(':checked');
        $('.item-checkbox').prop('checked', isChecked);
        updateTotalItemsAndPrice();  // 전체 선택에 따라 합계 업데이트
    });

    // 선택 삭제 버튼 클릭 핸들러
    document.getElementById('delete-selected-btn').addEventListener('click', () => {
        itemsToDelete = [];
        $('.item-checkbox:checked').each(function () {
            itemsToDelete.push($(this).data('item-id'));
        });
        if (itemsToDelete.length > 0) {
            document.getElementById('delete-confirm-modal').classList.remove('hidden');
        } else {
            alert('삭제할 항목을 선택하세요.');
        }
    });

    // PayPal 버튼을 설정
    paypal.Buttons({
        createOrder: function(data, actions) {
            return actions.order.create({
                purchase_units: [{
                    amount: {
                        value: calculateTotalAmount() // 결제 금액
                    }
                }]
            });
        },
        onApprove: function(data, actions) {
            return actions.order.capture().then(function(details) {
                alert('결제가 완료되었습니다!');
                // 결제 후 처리할 추가 작업
            });
        }
    }).render('#paypal-button-container'); // 결제 버튼이 렌더링될 컨테이너 ID
});

function loadCartItems() {
    $.ajax({
        url: `${contextPath}/cart/api/items`,
        method: 'GET',
        success: function (data) {
            renderCartItems(data);
        },
        error: function (error) {
            console.error('Error loading cart items:', error);
        }
    });
}

function renderCartItems(items) {
    const cartItemsContainer = $('#cart-items');
    cartItemsContainer.empty();

    items.forEach(item => {
        const itemTotal = item.lprice * item.quantity;

        const cartItem = `
            <div class="flex items-center hover:bg-gray-100 -mx-8 px-6 py-5" data-item-id="${item.id}">
                <div class="w-1/12 flex justify-center items-center">
                    <input type="checkbox" class="item-checkbox" data-item-id="${item.id}" onchange="updateTotalItemsAndPrice()">
                </div>
                <div class="flex items-center w-3/5"> <!-- 상품 상세 -->
                    <div class="w-20">
                        <img class="h-24" src="${item.image}" alt="${item.title}">
                    </div>
                    <div class="flex flex-col justify-between ml-4 flex-grow">
                        <span class="font-bold text-sm">${item.title}</span>
                    </div>
                </div>
                <div class="flex justify-center w-1/6">
                    <button class="text-gray-600" onclick="changeQuantity(${item.id}, -1)">-</button>
                    <input class="mx-2 border text-center w-8 item-quantity" type="text" value="${item.quantity}" readonly>
                    <button class="text-gray-600" onclick="changeQuantity(${item.id}, 1)">+</button>
                </div>
                <span class="text-center w-1/5 font-semibold text-sm item-price" data-price="${item.lprice}">${new Intl.NumberFormat().format(item.lprice)} 원</span>
                <span class="text-center w-1/5 font-semibold text-sm item-total">${new Intl.NumberFormat().format(itemTotal)} 원</span>
                <div class="w-1/12 text-center">
                    <button class="font-semibold hover:text-red-500 text-gray-500 text-xs" onclick="showDeleteConfirmModal(${item.id})">
                    삭제<img src="${contextPath}/images/delete1.png" alt="Delete Icon" class="inline-block w-4 h-4 ml-1">
                    </button>
                </div>
            </div>
        `;
        cartItemsContainer.append(cartItem);
    });

    updateTotalItemsAndPrice();  // 로드 후 초기 합계 업데이트
}


function changeQuantity(itemId, delta) {
    const itemElement = $(`[data-item-id='${itemId}']`);
    const quantityElement = itemElement.find(".item-quantity");
    let newQuantity = parseInt(quantityElement.val(), 10) + delta;

    if (newQuantity <= 0) {
        showDeleteConfirmModal(itemId);
        return;
    }

    updateCartItem(itemId, newQuantity);
}

function updateCartItem(itemId, newQuantity) {
    const itemElement = $(`[data-item-id='${itemId}']`);
    const priceElement = itemElement.find(".item-price");
    const totalElement = itemElement.find(".item-total");
    const quantityElement = itemElement.find(".item-quantity");

    const itemPrice = parseInt(priceElement.data('price'), 10) || 0;
    const newTotal = itemPrice * newQuantity || 0;

    // UI 즉시 업데이트
    quantityElement.val(newQuantity);
    totalElement.text(`${new Intl.NumberFormat().format(newTotal)} 원`);

    // 서버와 동기화
    $.ajax({
        url: `${contextPath}/cart/api/items/${itemId}`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({ quantity: newQuantity }),
        success: function () {
            updateTotalItemsAndPrice();  // 장바구니 항목과 결제 요약을 동시에 업데이트
        },
        error: function (error) {
            console.error('Error updating cart item:', error);
        }
    });
}

function confirmDelete() {
    if (itemsToDelete.length > 0) {
        itemsToDelete.forEach(itemId => {
            deleteCartItem(itemId);
        });
    } else if (itemToDeleteId) {
        deleteCartItem(itemToDeleteId);
    }
    closeDeleteConfirmModal();
}

function deleteCartItem(itemId) {
    $.ajax({
        url: `${contextPath}/cart/products/${itemId}`,
        method: 'DELETE',
        success: function () {
            loadCartItems();
            showSuccessModal();
        },
        error: function (error) {
            console.error('Error removing cart item:', error);
        }
    });
}

function updateTotalItemsAndPrice() {
    let totalItems = 0;
    let totalPrice = 0;

    $('#cart-items .flex').each(function () {
        const checkbox = $(this).find('.item-checkbox');
        if (checkbox.is(':checked')) {
            const quantity = parseInt($(this).find('.item-quantity').val(), 10) || 0;
            const itemPrice = parseInt($(this).find('.item-price').data('price'), 10) || 0;

            totalItems += quantity;
            totalPrice += quantity * itemPrice;
        }
    });

    $('#summary-item-count').text(totalItems + "건");
    updateTotalPrice(totalPrice);  // 상품에 따라 총합계 업데이트
}

function updateTotalPrice(baseTotalPrice = 0) {
    let shippingCost = 3000;  // 기본 배송비 설정

    if (baseTotalPrice >= 50000) {
        shippingCost = 0; // 50,000원 이상 구매 시 무료 배송
    }

    $('#shipping-cost-display').text(`${new Intl.NumberFormat().format(shippingCost)} 원`);

    const totalPrice = baseTotalPrice > 0 ? baseTotalPrice + shippingCost : 0;
    $('#total-price').text(`${new Intl.NumberFormat().format(totalPrice)} 원`);

    updateEstimatedUSDPrice(totalPrice); // 예상 결제 금액(USD) 업데이트
}

$(document).ready(function() {
    $('#shipping-cost-display').text('3,000 원');
    $('#total-price').text('0 원');  // 처음에 총합계는 0원으로 설정
});

function updateEstimatedUSDPrice(totalKRW) {
    // 무료 환율 API 사용 (exchangerate-api.com)
    $.ajax({
        url: 'https://v6.exchangerate-api.com/v6/7efdccfe77fd5338eff8fa5b/latest/KRW',
        method: 'GET',
        success: function(data) {
            const exchangeRate = data.conversion_rates.USD; // KRW에서 USD로 환율
            const estimatedUSD = (totalKRW * exchangeRate).toFixed(2);
            $('#estimated-usd-price').text(`$${estimatedUSD}`);
        },
        error: function(error) {
            console.error('Error fetching exchange rate:', error);
        }
    });
}

function calculateTotalAmount() {
    const totalPriceText = $('#total-price').text();
    const totalPrice = parseInt(totalPriceText.replace(/[^0-9]/g, ''), 10) || 0;
    return (totalPrice / 1000).toFixed(2); // USD로 환산
}

function showDeleteConfirmModal(itemId) {
    itemToDeleteId = itemId;
    document.getElementById('delete-confirm-modal').classList.remove('hidden');
}

function closeDeleteConfirmModal() {
    itemToDeleteId = null;
    document.getElementById('delete-confirm-modal').classList.add('hidden');
}

function showSuccessModal() {
    document.getElementById('delete-success-modal').classList.remove('hidden');
}

function closeSuccessModal() {
    document.getElementById('delete-success-modal').classList.add('hidden');
}
