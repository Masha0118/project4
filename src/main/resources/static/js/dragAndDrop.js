$(function() {
    $(".product-item").draggable({
        revert: "invalid",
        containment: "document",
        helper: "clone",
        cursor: "move",
        start: function(event, ui) {
            // 드래그가 시작될 때 드래그 중인 요소의 정보 저장
            ui.helper.data("originalPosition", ui.helper.position());
        },
        stop: function(event, ui) {
            // 드래그가 종료되면 모든 카테고리의 높이를 점검
            $(".horizontal-scroll").each(function() {
                const droppable = $(this);
                const originalHeight = droppable.data("originalHeight");

                // 자식 요소가 없으면 원래 높이로 복원
                if (droppable.children(".product-item").length === 0) {
                    droppable.height(originalHeight);
                }
            });
        }
    });

    $(".horizontal-scroll").each(function() {
        // 초기 높이를 저장
        $(this).data("originalHeight", $(this).height());
    }).droppable({
        accept: ".product-item",
        classes: {
            "ui-droppable-active": "ui-state-highlight"
        },
        over: function(event, ui) {
            const draggableHeight = ui.helper.outerHeight(); // 드래그 중인 요소의 높이
            const droppable = $(this);

            // 현재 높이
            const currentHeight = droppable.height();

            // 새로운 높이로 조정 (드래그 중인 요소의 높이가 현재 높이보다 크면 업데이트)
            if (draggableHeight > currentHeight) {
                droppable.height(draggableHeight);
            }
        },
        drop: function(event, ui) {
            const categoryElement = $(this).closest(".category");
            const droppedCategoryId = categoryElement.find("input[class^='category-id-']").val();
            console.log("Dropped Category ID:", droppedCategoryId);

            if (!droppedCategoryId) {
                console.error("Dropped Category ID is undefined or null");
                return;
            }

            const productId = ui.draggable.attr('id').split('-')[1];
            console.log("Product ID:", productId);

            $(this).append(ui.draggable);

            // 드래그된 요소가 원래 다른 .horizontal-scroll에서 이동된 것인지 확인
            if (ui.draggable.data("ui-draggable") && ui.draggable.data("ui-draggable").originalPosition) {
                saveProductPosition(productId, droppedCategoryId);
            }
        }
    });
});

function saveProductPosition(productId, droppedCategoryId) {
    console.log("Saving product position", productId, droppedCategoryId);
    fetch(`${contextPath}/wishlist/products/move`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ productId: productId, droppedCategoryId: droppedCategoryId })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(data => {
            console.log('Product moved successfully:', data);
        })
        .catch(error => {
            console.error('Error moving product:', error);
            alert('제품 이동 중 오류가 발생했습니다: ' + error.message);
        });
}

