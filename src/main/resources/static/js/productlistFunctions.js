let dataContainer;
let contextPath;

document.addEventListener('DOMContentLoaded', () => {
    let selectedCategory = '할인'; // 기본 값

    dataContainer = document.getElementById('data-container');
    contextPath = dataContainer.getAttribute('data-my-variable') || "";
    contextPath = contextPath === null ? "" : contextPath;

    console.log(contextPath);

    const categories = {
        "생활/건강": {
            "자동차용품": {
                "세차용품": ["차량용커버","호스", "광택기", "세차패키지", "차량용브러시", "세차타월", "도장용광택/코팅제", "세차스펀지", "차량용컴프레서", "차량용먼지떨이개", "컴파운드", "실내세정제", "스티커/타르제거제", "세차물통", "카샴푸", "세차장갑", "유리복원제", "유리코팅제", "타이어세정광택제", "유리세정제", "김서림방지제", "휠세정/광택제", "엔진룸세척", "정전기방지", "유막제거제"],
                "수납용품": ["트렁크정리함", "차량용포켓", "차량용휴지통", "콘솔박스", "차량용홀더", "차량용옷걸이/후크/행거", "선글라스클립", "논슬립패드", "차량용테이블", "차량용재떨이", "차량용우산걸이", "CD바이저"],
                "타이어/휠": ["타이어용품", "휠용품", "휠", "스노우체인", "타이어"],
                "편의용품": ["차량용청소기", "차량용공기청정기", "차량용선풍기", "차량용온풍기", "차량용냉/온장고", "차량용면도기", "차량용보온컵", "차량용가습기"],
                "튜닝용품": ["기타튜닝용품", "배기/흡기튜닝", "서스펜션", "냉각", "엔진튠업", "게이지", "브레이크", "혼", "계기판", "레이싱용품"],
                "인테리어용품": ["차량용장식소품", "바닥매트", "내부몰딩", "차량용햇빛가리개", "핸들커버", "주차번호판", "안전벨트용품", "썬팅필름", "시트커버", "방음/방진/흡음", "차량용목쿠션", "차량용팔/틈새쿠션", "페달용품", "대시보드커버", "핸들", "차량용시계", "보조룸미러", "차량용등쿠션", "기어용품", "룸미러", "차량용방석", "룸미러커버", "차량용나침반", "실내원단", "차량용사진액자"],
                "익스테리어용품": ["사이드스텝", "가드", "루프박스", "안테나/안테나볼", "몰딩", "에어로파츠", "차량용스티커", "도어스커프", "사이드미러", "엠블럼", "선바이저", "캐리어", "번호판볼트", "후사경", "차량용폴대", "후드핀"],
                "DIY용품": ["도색용품", "차량용공구", "차량용보호필름", "차량용시트지", "차량용스위치", "DIY전구", "접착제", "LED소자", "레진"],
                "오일/소모품": ["와이퍼", "에어컨필터", "워셔액", "연료첨가제", "기타 오일/소모품", "엔진오일", "부동액", "엔진오일첨가제", "브레이크오일", "오일필터", "미션오일", "요소수", "연료필터", "에어필터", "오일세트", "미션오일첨가제"],
                "전기용품": ["기타전기용품", "소켓/시거잭", "휴즈", "인버터", "차량용케이블", "전압안정기", "릴레이", "점화플러그", "차량용태양열충전기", "차량용AC어댑터", "점화케이블", "컨버터"],
                "공기청정용품": ["차량용방향제", "차량용탈취제"],
                "램프": ["기타램프용품", "전조등", "차량용실내등", "테일램프", "안개등", "방향지시등"],
                "휴대폰용품": ["차량용휴대폰거치대", "차량용휴대폰충전기"],
                "배터리용품": ["배터리충전기/점프스타터", "점프선", "기타배터리용품", "터미널", "차량용배터리"],
                "키용품": ["키케이스", "스타트버튼", "시동경보기", "스마트키", "폴딩키", "스킨"]
            },
            "수집품": {
                "무선/RC": ["자동차", "RC소품", "중장비", "드론", "탱크", "보트/배", "헬기", "비행기"],
                "모형/프라모델/피규어": ["다이캐스트", "피규어", "프라모델", "보관용품", "모형", "프라모델용품", "밀리터리"],
                "퍼즐/블록": ["블록", "퍼즐"],
                "게임": ["보드게임", "마술도구", "다트"],
                "공예품": ["공예품"],
                "이색수집품": ["이색수집품"],
                "서바이벌": ["서바이벌용품", "건(GUN)", "보호장비"],
                "코스튬플레이": ["의상", "소품"],
                "아이돌굿즈": ["문구사무용품", "텀블러/물병", "기타굿즈", "포토/브로마이드", "스티커", "휴대폰용품", "응원봉"],
                "화폐": ["주화", "지폐"],
                "우표/씰": ["우표", "씰"],
                "LP": ["LP"]
            },
            "공구": {
                "수작업공구": ["기타수작업공구", "공구세트", "렌치", "공구함", "펜치", "복스", "드라이버", "플라이어", "스패너", "망치", "작업대", "몽키", "도끼", "핀셋", "니퍼", "바이스", "타카", "타카핀"],
                "안전용품": ["기타안전용품", "안전표시/신호", "비상/구난용품", "소화기", "안전/작업복", "안전장갑", "소방용품", "안전모", "보안경", "마스크"],
                "측정공구": ["기타측정기", "테스트기", "온도계/습도계", "레이저측정기", "캘리퍼스", "저울", "음주측정기", "수평기", "워킹자/줄자", "조도계", "산소포화도측정기", "염도계", "흡연측정기", "방사능측정기"],
                "전기용품": ["기타 전기용품", "충전용 건전지/배터리", "전선/케이블", "일회용 리튬건전지", "일회용 건전지", "멀티탭", "전선정리용품"],
                "에어용품": ["유압공구", "에어스프레이건", "에어호스/게이지", "에어건", "에어렌치", "컴프레서", "기타에어공구", "에어타카"],
                "소형기계": ["모터", "발전기", "펌프", "기타소형기계"],
                "전동공구": ["기타전동공구", "송풍기", "광택기", "전동공구액세서리", "충전드릴", "열풍기", "청소기", "비트세트", "전동드릴", "해머드릴", "드릴세트", "컷쏘"],
                "운반용품": ["핸드카트/운반기", "사다리", "지게차"],
                "용접공구": ["용접기", "용접용품", "용접봉", "인두기", "인두용품", "해빙기", "절단기"],
                "원예공구": ["압축분무기", "호스/호스카트", "농기구", "농기계", "스프링클러", "기타원예공구", "잔디깎이", "비료살포기", "원예가위"],
                "설비공구": ["배관용품", "컷터기", "확관기", "파이프머신", "코어드릴"],
                "목공공구": ["샌더", "톱", "대패", "조각기", "트리머", "홀쏘"],
                "체결용품": ["볼트/너트", "홀딩클램프", "리벳", "나사", "스프링", "핀", "못"],
                "접착용품": ["윤활유", "접착제", "글루건", "실리콘", "세정제/클리너", "스프레이", "시멘트/몰탈"],
                "페인트용품": ["기타페인트용품", "붓", "롤러", "방수제", "에폭시", "젯소", "스테인", "바니시"],
                "절삭공구": ["그라인더", "그라인더날", "각도/고속절단기", "다용도조각기", "루터날", "직소기", "루터", "직소기날"],
                "페인트": ["락카/스프레이페인트", "수성페인트", "유성페인트"],
                "예초기": ["예초기부품", "엔진예초기", "충전/전기예초기", "가스예초기"],
                "포장용품": ["포장지", "리본", "선물박스", "밴딩기", "커터", "택배박스", "택배봉투"]
            },
            "문구/사무용품": {
                "스티커/테이프": ["테이프", "스티커", "마스킹테이프", "네임스티커"],
                "이벤트/파티용품": ["데코용품", "풍선/풍선용품", "케이크토퍼", "가면/머리띠", "식기/테이블용품", "가랜드", "태극기", "야광용품", "고깔모자", "폭죽/불꽃놀이"],
                "문구용품": ["기타문구용품", "클립/핀", "사무용칼", "스테이플러/침", "자석", "지우개", "문구세트", "자", "풀/접착제", "모양펀치", "펀치", "북마크", "사무용가위", "다이모/엠보서", "집게", "수정용품"],
                "필기도구": ["연필깎이", "보드마카", "붓펜", "필통", "볼펜", "기타필기도구", "펜", "사인펜", "형광펜", "연필", "분필", "만년필", "매직", "샤프", "네임펜", "잉크", "리필심", "필기구세트", "샤프심"],
                "사무기기": ["금고", "RFID", "일반계산기", "코팅기", "제본기", "문서세단기", "재단기", "출퇴근기록기", "지폐계수기", "공학용계산기", "동전계수기", "천공기", "금전등록기", "접지기", "디지털복사기", "OHP", "전자타자기", "팩스", "실물화상기"],
                "데스크용품": ["저금통", "명함꽂이", "메모꽂이", "데스크정리함", "문서보관함/서류함", "연필꽂이", "서류꽂이/서류받침대", "독서대", "데스크패드", "북엔드", "문진"],
                "사무용품": ["현수막", "명찰", "명함/전단지제작", "상패", "명패"],
                "다이어리/플래너": ["캘린더/달력", "바인더", "가계부", "스케줄러/플래너", "다이어리", "속지/포켓"],
                "보드/칠판": ["화이트보드", "네온보드", "전자칠판", "코르크/게시판", "칠판", "메모판/미니보드", "블랙보드"],
                "파일/바인더": ["바인더", "파일", "서류철/결재판", "파일속지"],
                "노트/수첩": ["점착식메모지", "노트", "메모지", "노트패드", "수첩"],
                "용지": ["라벨지", "양식/서식지", "코팅지/필름지", "기타용지", "복사지", "인화지/포토용지", "컬러복사지", "견출지", "팩스용지/전산용지"],
                "제도용품": ["기타제도용품", "제도용필기구", "제도용자", "제도용지", "제도판"],
                "카드/엽서/봉투": ["카드", "봉투", "엽서", "청첩장", "편지지", "모바일초대장"],
                "스탬프/도장": ["스탬프", "도장", "인주", "스탬프패드"],
                "지도/지구본": ["지구본", "국내지도", "세계지도"],
                "앨범": ["포켓식앨범", "접착식앨범", "폴라로이드앨범", "포토박스"]
            }
        },
        "스포츠/레저": {
            "낚시": {
                "낚시용품": ["기타낚시용품", "태클박스", "도래", "봉돌/싱커", "받침대/받침틀/뒤꽂이", "가위/라인커터/핀온릴", "케미/집어등", "뜰채/뜰망", "어군탐지기", "밑밥통/살림통", "플라이어", "쿨백", "고기집게", "낚시의자", "밑밥주걱", "두레박", "가프", "포셉", "기포기", "살림망/꿰미", "낚시텐트", "낚시칼", "통발/그물", "바늘결속기"],
                "낚싯대": ["바다루어낚싯대", "바다찌낚싯대", "민물낚싯대", "바다선상낚싯대", "민물루어낚싯대", "바다원투낚싯대", "바다민장대", "플라이낚싯대"],
                "루어낚시": ["하드베이트", "소프트베이트", "바늘", "루어낚시세트", "루어가방", "루어로드케이스"],
                "낚시릴": ["스피닝릴", "베이트릴", "전동릴", "플라이릴"],
                "바다낚시": ["찌", "바다낚싯바늘", "바다낚시가방", "바다낚시세트", "밑밥", "찌케이스"],
                "낚싯줄": ["합사라인", "모노라인", "카본라인", "플라이라인"],
                "낚시의류/잡화": ["낚시복", "낚시모자", "낚시장화", "낚시장갑", "낚시조끼", "낚시신발", "힙커버/힙가드"],
                "민물낚시": ["찌", "떡밥", "민물낚싯바늘", "민물낚시가방", "얼음낚시", "찌케이스", "민물낚시세트"]
            }
        },
        "디지털/가전": {
            "휴대폰액세서리": {
                "휴대폰케이스": ["갤럭시 케이스", "아이폰 케이스", "기타휴대폰케이스", "암밴드"],
                "휴대폰보호필름": ["액정보호필름", "전신보호필름"]
            }
        },
        "출산/육아": {
            "유아동의류": {
                "티셔츠": ["티셔츠"],
                "완구/인형": ["기타미술놀이", "색연필", "클레이", "크레파스", "물감", "종이접기", "스케치북", "색칠공부", "이젤", "크레욜라", "그림판", "플레이콘/매직콘"]
            }
        },
        "가구/인테리어": {
            "인테리어소품": {
                "조명": ["인테리어조명", "전구", "야외조명", "벽등", "거실조명", "현관조명", "LED모듈", "형광등", "주방조명", "욕실조명", "샹들리에"],
                "액자": ["퍼즐/그림/사진액자", "탁상용액자", "벽걸이액자", "액자세트", "포토보드/집게", "파티션액자", "캐릭터액자"]
            }
        },
        "패션잡화": {
            "패션소품": {
                "우산": ["자동우산", "수동우산", "기타"],
                "브로치": ["패션브로치", "카네이션브로치", "보석브로치"]
            }
        },
        "화장품/미용": {
            "네일케어": {
                "네일아트": ["네일아트"],
                "매니큐어/젤네일": ["매니큐어/젤네일"]
            }

        },
        "패션의류": {
            "남성의류": {
                "티셔츠": ["티셔츠"],
                "바지": ["바지"]
            }
        },
        "식품": {
            "건강식품": {
                "영양제": ["프로바이오틱스"],
                "비타민제": ["멀티비타민"]
            }
        },
        "여가/생활편의": {
            "국내여행/체험": {
                "공연/티켓": ["클래식/오페라", "뮤지컬", "연극", "콘서트"],
                "액티비티": ["워터파크/스파", "테마/놀이동산"]
            }
        }
    };

    // 공개/비공개 관련 핸들러
    let privateButton = document.getElementById('default-checkbox');
    let temp = "private";
    const checkbox = document.getElementById('default-checkbox');
    const isChecked = checkbox.checked;

    if (isChecked) {
        temp = checkbox.value;
    }

    privateButton.addEventListener("change", () => {
        const isChecked = checkbox.checked;
        if (!isChecked) {
            temp = "public";
        } else {
            temp = checkbox.value; // 체크박스가 선택된 상태일 때 temp를 업데이트
        }
        console.log(temp); // 디버깅을 위해 temp 값을 출력합니다.
    });

    // 아코디언 생성 함수
    function createAccordion(categories) {
        const container = document.getElementById('category-container');
        for (let mainCategory in categories) {
            let mainButton = document.createElement('button');
            mainButton.className = 'transition-colors duration-200 accordion-header bg-gray-100 text-black py-2 px-3 border border-gray-300 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50';
            mainButton.innerText = mainCategory;
            container.appendChild(mainButton);

            let subContainer = document.createElement('div');
            subContainer.className = 'accordion-content hidden flex flex-col space-y-2 mt-2 pl-4';
            container.appendChild(subContainer);

            for (let subCategory in categories[mainCategory]) {
                let subButton = document.createElement('button');
                subButton.className = 'accordion-subheader bg-blue-400 text-white py-2 px-4 rounded-lg hover:bg-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-400 focus:ring-opacity-50';
                subButton.innerText = subCategory;
                subContainer.appendChild(subButton);

                let subSubContainer = document.createElement('div');
                subSubContainer.className = 'accordion-subcontent hidden flex flex-col space-y-2 mt-2 pl-4';
                subContainer.appendChild(subSubContainer);

                for (let finalCategory in categories[mainCategory][subCategory]) {
                    let finalButton = document.createElement('button');
                    finalButton.className = 'accordion-subheader bg-blue-300 text-white py-2 px-4 rounded-lg hover:bg-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-300 focus:ring-opacity-50';
                    finalButton.innerText = finalCategory;
                    subSubContainer.appendChild(finalButton);

                    let finalSubContainer = document.createElement('div');
                    finalSubContainer.className = 'accordion-subcontent hidden flex flex-col space-y-2 mt-2 pl-4';
                    subSubContainer.appendChild(finalSubContainer);

                    categories[mainCategory][subCategory][finalCategory].forEach(item => {
                        let itemButton = document.createElement('button');
                        itemButton.className = 'bg-blue-300 text-white py-2 px-4 rounded-lg hover:bg-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-300 focus:ring-opacity-50';
                        itemButton.innerText = item;
                        itemButton.setAttribute('data-category', item);
                        finalSubContainer.appendChild(itemButton);
                    });
                }
            }
        }
    }

    createAccordion(categories);

    // 아코디언 기능 구현
    document.addEventListener('click', function (event) {
        if (event.target.matches('.accordion-header')) {
            event.target.nextElementSibling.classList.toggle('hidden');
        }

        if (event.target.matches('.accordion-subheader')) {
            event.target.nextElementSibling.classList.toggle('hidden');
        }
    });

    // 카테고리 버튼 클릭 이벤트 리스너
    const categoryButtons = document.querySelectorAll('button[data-category]');
    categoryButtons.forEach(button => {
        button.addEventListener('click', (event) => {
            selectedCategory = event.target.getAttribute('data-category');
            fetchAndRenderProducts(); // 카테고리 변경시 상품 목록 갱신
        });
    });

    // 비교 그룹 저장을 위한 배열 초기화
    const comparisonGroups = [];

    // API 호출 및 결과 렌더링 함수
    function fetchAndRenderProducts() {
        const urlParams = new URLSearchParams(window.location.search);
        const keyword = urlParams.get('keyword');

        if (selectedCategory || keyword) {
            const searchKeyword = keyword || selectedCategory;
            const uri = `${contextPath}/productlists?keyword=${encodeURIComponent(searchKeyword)}&quantity=100`;

            fetch(uri)
                .then(response => {
                    if (response.status === 429) { // HTTP 429 상태 코드 확인
                        return response.text().then(text => {
                            // 경고창 표시
                            alert("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
                            throw new Error('Too Many Requests');
                        });
                    }

                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }

                    return response.json();
                })
                .then(data => {
                    renderProducts(data);
                })
                .catch(error => {
                    console.error('Error fetching products:', error);
                });
        }
    }

    // 상품 목록 렌더링 함수
    function renderProducts(products) {
        const productList = document.getElementById('product-list');
        productList.innerHTML = ''; // 기존 내용 초기화

        products.forEach(product => {
            const productItem = document.createElement('div');
            productItem.className = 'bg-white p-4 rounded-lg shadow-md relative flex flex-col justify-between';


            productItem.innerHTML = `
                <input type="checkbox" class="compare-checkbox appearance-none border border-gray-300 rounded-full w-5 h-5 mb-2 bg-white checked:bg-blue-500 checked:border-transparent focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 mt-1" data-id="${product.productId}" data-title="${product.title}" data-image="${product.image}" data-price="${product.lprice}" data-link="${product.link}">
                <form method="post" class="flex flex-col justify-between h-full">
                    <a href="${product.link}" target="_blank" class="relative overflow-hidden">
                        <img src="${product.image}" alt="${product.title}" class="w-full h-48 object-cover mb-4 rounded-lg transition-transform duration-300 ease-in-out transform hover:scale-110">
                        <h3 class="text-lg font-semibold mb-2">${product.title}</h3>
                    </a>
                    <div class="flex flex-col mt-auto">
                        <div class="flex justify-between items-center">
                            <p class="text-gray-600">${new Intl.NumberFormat().format(product.lprice)} 원</p>
                        </div>
                        <button type="button" class="transition-colors duration-200 wishlist-btn flex items-center justify-center text-black py-2 px-4 mt-2 rounded-lg border-2 border-gray-300 border-solid hover:border-2 hover:border-red-300 hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50"
                            data-id="${product.productId}" data-title="${product.title}" data-lprice="${product.lprice}" data-link="${product.link}" data-image="${product.image}" data-category="${product.category3}">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-red-500" viewBox="0 0 20 20" fill="currentColor">
                                <path d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 015.656 5.656l-1.172 1.172L10 17.485l-5.656-5.656-1.172-1.172a4 4 0 010-5.656z" />
                            </svg>
                            위시리스트
                        </button>
						<button type="button" class="transition-colors duration-200 cart-btn flex items-center justify-center text-black py-2 px-4 mt-2 rounded-lg border-2 border-gray-300 border-solid bg-white hover:border-2 hover:border-blue-300 hover:bg-blue-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50"
						    data-id="${product.productId}" data-title="${product.title}" data-lprice="${product.lprice}" data-link="${product.link}" data-image="${product.image}">
						    <img src="${contextPath}/images/cart1.png" alt="Cart Icon" class="w-6 h-6">
						</button>
                    </div>
                </form>
            `;


            productList.appendChild(productItem);
			
			



			
			
			
			
			

            // 위시리스트 버튼 클릭 시 로그인 여부 확인
            const wishlistButton = productItem.querySelector('.wishlist-btn');
            wishlistButton.addEventListener('click', (event) => {
                event.preventDefault(); // 기본 동작 막기

                // 로그인 여부 확인 (서버에서 제공하는 변수 또는 API 호출)
                fetch(`${contextPath}/user/logincheck`)
                    .then(response => response.json())
                    .then(isAuthenticated => {
                        if (!isAuthenticated) {
                            // 로그인되지 않은 경우 로그인 페이지로 리디렉션
                            window.location.href = `${contextPath}/user/login`;
                        } else {
                            // 로그인된 경우 모달창 열기
                            const productData = {
                                productId: event.currentTarget.getAttribute('data-id'),
                                title: event.currentTarget.getAttribute('data-title'),
                                lprice: event.currentTarget.getAttribute('data-lprice'),
                                link: event.currentTarget.getAttribute('data-link'),
                                image: event.currentTarget.getAttribute('data-image'),
                                category3: event.currentTarget.getAttribute('data-category'),
                            };

                            // 모달창에 JSON 데이터 설정
                            document.getElementById('wishlist-modal').dataset.productData = JSON.stringify(productData);
                            document.getElementById('wishlist-modal').classList.remove('hidden');
                        }
                    })
                    .catch(error => {
                        console.error('Error checking authentication:', error);
                    });
            });

			document.querySelectorAll('.cart-btn').forEach(button => {
			    button.addEventListener('click', function(event) {
					event.preventDefault(); // 기본 동작 막기

					                // 로그인 여부 확인 (서버에서 제공하는 변수 또는 API 호출)
					                fetch(`${contextPath}/user/logincheck`)
					                    .then(response => response.json())
					                    .then(isAuthenticated => {
					                        if (!isAuthenticated) {
					                            // 로그인되지 않은 경우 로그인 페이지로 리디렉션
					                            window.location.href = `${contextPath}/user/login`;
					                        }
			    });
			});
			});
            // 장바구니 버튼 클릭 시 상품을 장바구니에 추가
            const cartButton = productItem.querySelector('.cart-btn');
            cartButton.addEventListener('click', (event) => {
                event.preventDefault(); // 기본 동작 막기

                const productData = {
                    productId: event.currentTarget.getAttribute('data-id'),
                    title: event.currentTarget.getAttribute('data-title'),
                    lprice: event.currentTarget.getAttribute('data-lprice'),
                    link: event.currentTarget.getAttribute('data-link'),
                    image: event.currentTarget.getAttribute('data-image'),
                    quantity: 1 // 기본 수량 설정
                };

                // 장바구니 API 호출
                fetch(`${contextPath}/cart/add`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(productData)
                })
                    .then(response => {
                        if (response.status === 401) {
                            // 로그인되지 않은 경우 로그인 페이지로 이동
                            window.location.href = "/user/login";
                        } else if (response.ok) {
                            openCartModal(); // 장바구니 추가 후 모달 열기
                        } else {
                            alert('상품 추가에 실패했습니다.');
                        }
                    })
                    .catch(error => console.error('Error adding product to cart:', error));
            });

        });

        // "위시리스트" 버튼 클릭 시 모달창 표시
        document.querySelectorAll('.wishlist-btn').forEach(button => {
            button.addEventListener('click', (event) => {
                event.preventDefault(); // 폼 제출 기본 동작 막기

                // 상품 데이터를 가져와서 모달창에 전달
                const productData = {
                    productId: event.currentTarget.getAttribute('data-id'),
                    title: event.currentTarget.getAttribute('data-title'),
                    lprice: event.currentTarget.getAttribute('data-lprice'),
                    link: event.currentTarget.getAttribute('data-link'),
                    image: event.currentTarget.getAttribute('data-image'),
                    category3: event.currentTarget.getAttribute('data-category'),
                };

                // 모달창에 JSON 데이터 설정
                document.getElementById('wishlist-modal').dataset.productData = JSON.stringify(productData);
                document.getElementById('wishlist-modal').classList.remove('hidden');
            });
        });


        // 페이지 로드 시 서버에서 비교 이력을 불러와서 렌더링
        fetchComparisonHistory();

        document.getElementById('compare-button').addEventListener('click', () => {
            const selectedProducts = [];
            document.querySelectorAll('.compare-checkbox:checked').forEach(checkbox => {
                selectedProducts.push({
                    productId: checkbox.dataset.id,
                    title: checkbox.dataset.title,
                    image: checkbox.dataset.image,
                    price: checkbox.dataset.price,
                    link: checkbox.dataset.link
                });
            });

            if (selectedProducts.length < 2) {
                alert('비교하려면 최소 두 개의 상품을 선택하세요.');
                return;
            }

            // 비교 그룹을 저장
            saveComparisonGroup(comparisonGroups.length, selectedProducts);

            document.querySelectorAll('.compare-checkbox:checked').forEach(checkbox => {
                checkbox.checked = false;
            });
        });
    }

    // 모달 창을 열기 위한 함수
    function openCartModal() {
        document.getElementById('cart-modal').classList.remove('hidden');
    }

    // 모달 창을 닫기 위한 함수
    function closeModal() {
        document.getElementById('cart-modal').classList.add('hidden');
    }

    // 모달 창의 버튼 이벤트 핸들러
    document.getElementById('go-to-cart-btn').addEventListener('click', () => {
        window.location.href = `${contextPath}/cart`;
    });

    document.getElementById('continue-shopping-btn').addEventListener('click', () => {
        window.location.href = `${contextPath}/productlist`;
        closeModal();
    });

    // 비교 그룹을 저장하고 comparisonGroups에 추가하는 함수
    function saveComparisonGroup(groupIndex, selectedProducts) {
        // 서버에 비교 이력 저장 요청
        fetch(`${contextPath}/comparison/save`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(selectedProducts)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log('Comparison history saved:', data);

                // 그룹 ID를 포함하여 comparisonGroups에 저장
                comparisonGroups[groupIndex] = {
                    id: data.id,
                    products: data.products
                };

                // 사이드바에 비교 이력을 추가
                addToComparisonHistory(data.products);

                renderComparisonTable(data.products);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('로그인 후 이용하실 수 있습니다.');
            });
    }

    // 서버에서 비교 이력을 불러오는 함수
    function fetchComparisonHistory() {
        fetch(`${contextPath}/comparison/history`, {
            method: 'GET',
            credentials: 'include', // 인증 쿠키 포함
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => {
                const contentType = response.headers.get('content-type');
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                if (!contentType || !contentType.includes('application/json')) {
                    throw new TypeError("Received response is not JSON");
                }
                return response.json();
            })
            .then(data => {
                console.log('Fetched comparison history:', data);
                if (!data || data.length === 0) {
                    console.log('No comparison history found.');
                    return;
                }
                data.forEach(history => {
                    const products = history.products.map(product => ({
                        productId: product.productId || product.id || 'unknown-id',
                        title: product.title,
                        image: product.image,
                        price: product.price,
                        link: product.link
                    }));
                    comparisonGroups.push({
                        id: history.id,
                        products: products
                    });
                    addToComparisonHistory(products);
                });
            })
            .catch(error => {
                console.error('Error fetching comparison history:', error.message, error);
            });
    }

    // 사이드바에 비교 이력을 추가하는 함수
    function addToComparisonHistory(products) {
        const historyContainer = document.getElementById('comparison-history');
        const groupIndex = comparisonGroups.length - 1;

        const historyItem = document.createElement('div');
        historyItem.className = 'bg-white p-4 rounded-lg shadow-md cursor-pointer hover:bg-gray-100 mb-4 relative';

        // 비교 그룹 제목
        historyItem.innerHTML = `<div class="font-bold mb-2">비교 그룹 ${groupIndex + 1} (${products.length}개 상품)</div>`;

        // X 버튼 추가
        const closeButton = document.createElement('button');
        closeButton.className = 'absolute top-2 right-2 text-gray-600 hover:text-gray-900';
        closeButton.innerHTML = '&times;';
        closeButton.addEventListener('click', (event) => {
            event.stopPropagation();  // 그룹 박스 클릭 이벤트 전파 막기
            deleteComparisonGroup(groupIndex, historyItem);
        });

        historyItem.appendChild(closeButton);

        // 상품 이미지들을 추가 (4개씩 줄바꿈)
        const imageContainer = document.createElement('div');
        imageContainer.className = 'grid grid-cols-4 gap-2'; // 한 줄에 4개의 이미지를 배치하도록 설정

        products.forEach(product => {
            const imgElement = document.createElement('img');
            imgElement.src = product.image;
            imgElement.alt = product.title;
            imgElement.className = 'w-12 h-12 object-cover rounded mb-2';
            imgElement.dataset.id = product.productId || product.id || 'unknown-id';
            imageContainer.appendChild(imgElement);
        });

        historyItem.appendChild(imageContainer);

        historyItem.addEventListener('click', () => {
            // 그룹 ID로 현재 그룹의 인덱스를 찾음
            const currentGroupId = comparisonGroups[groupIndex]?.id;

            if (currentGroupId === undefined) {
                console.error('Invalid group index:', groupIndex);
                alert('해당 비교 그룹을 찾을 수 없습니다.');
                return;
            }

            const currentGroupIndex = comparisonGroups.findIndex(group => group.id === currentGroupId);

            // currentGroupIndex가 유효한지 확인
            if (currentGroupIndex >= 0 && currentGroupIndex < comparisonGroups.length) {
                // 유효한 경우에만 renderComparisonTable 호출
                renderComparisonTable(comparisonGroups[currentGroupIndex].products);
                document.getElementById('compare-modal').classList.remove('hidden');
            } else {
                console.error('Invalid group index:', currentGroupIndex);
                alert('해당 비교 그룹을 찾을 수 없습니다.');
            }
        });

        historyContainer.appendChild(historyItem);
    }

    // 비교 그룹 삭제 함수
    function deleteComparisonGroup(groupIndex, historyItem) {

        if (groupIndex >= comparisonGroups.length || groupIndex < 0) {
            console.error('Invalid group index:', groupIndex);
            alert('해당 비교 그룹을 찾을 수 없습니다.');
            return;
        }

        const comparisonHistoryId = comparisonGroups[groupIndex].id;


        console.log('Attempting to delete group at index:', groupIndex);
        console.log('Current comparison groups:', comparisonGroups);

        fetch(`${contextPath}/comparison/delete/${comparisonHistoryId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log('Successfully deleted group:', comparisonGroups[groupIndex]);

                // 그룹을 배열에서 삭제
                comparisonGroups.splice(groupIndex, 1);

                // UI를 초기화하고 다시 그리기
                const historyContainer = document.getElementById('comparison-history');
                historyContainer.innerHTML = ''; // 기존 UI를 초기화

                // 남은 그룹을 다시 그리기
                comparisonGroups.forEach((group, index) => {
                    const historyItem = document.createElement('div');
                    historyItem.className = 'bg-white p-4 rounded-lg shadow-md cursor-pointer hover:bg-gray-100 mb-4 relative';

                    historyItem.innerHTML = `<div class="font-bold mb-2">비교 그룹 ${index + 1} (${group.products.length}개 상품)</div>`;

                    const closeButton = document.createElement('button');
                    closeButton.className = 'absolute top-2 right-2 text-gray-600 hover:text-gray-900';
                    closeButton.innerHTML = '&times;';
                    closeButton.addEventListener('click', (event) => {
                        event.stopPropagation(); // 그룹 박스 클릭 이벤트 전파 막기
                        deleteComparisonGroup(index, historyItem); // 재귀 호출로 삭제
                    });

                    historyItem.appendChild(closeButton);

                    const imageContainer = document.createElement('div');
                    imageContainer.className = 'grid grid-cols-4 gap-2';

                    group.products.forEach(product => {
                        const imgElement = document.createElement('img');
                        imgElement.src = product.image;
                        imgElement.alt = product.title;
                        imgElement.className = 'w-12 h-12 object-cover rounded mb-2';
                        imageContainer.appendChild(imgElement);
                    });

                    historyItem.appendChild(imageContainer);

                    historyItem.addEventListener('click', () => {
                        renderComparisonTable(group.products);
                        document.getElementById('compare-modal').classList.remove('hidden');
                    });

                    historyContainer.appendChild(historyItem);
                });

                alert('비교 그룹이 삭제되었습니다.');

                if (comparisonGroups.length > 0) {
                    renderComparisonTable(comparisonGroups[0], false);
                } else {
                    // 비교 그룹이 없으면 테이블과 모달을 숨김
                    document.getElementById('comparison-header').innerHTML = '';
                    document.getElementById('comparison-table-body').innerHTML = '';
                    document.getElementById('compare-modal').classList.add('hidden');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('비교 그룹을 삭제하는 중 오류가 발생했습니다.');
            });
    }

    // 비교 테이블 렌더링 함수
    function renderComparisonTable(selectedProducts, showModal = true) {
        // selectedProducts가 배열이 아닌 경우 빈 배열로 초기화
        if (!Array.isArray(selectedProducts)) {
            selectedProducts = [];
        }

        const comparisonHeader = document.getElementById('comparison-header');
        const comparisonTableBody = document.getElementById('comparison-table-body');

        // 초기화
        comparisonHeader.innerHTML = '<th class="px-4 py-2">항목</th>'; // "항목" 열 추가
        comparisonTableBody.innerHTML = '';

        // 항목별 행 추가
        const imgRow = document.createElement('tr');
        const nameRow = document.createElement('tr');
        const priceRow = document.createElement('tr');
        const linkRow = document.createElement('tr');
        const addRow = document.createElement('tr'); // 추가 행 생성

        imgRow.innerHTML = '<td class="px-4 py-2 bg-gray-100 font-semibold">이미지</td>';
        nameRow.innerHTML = '<td class="px-4 py-2 bg-gray-100 font-semibold">이름</td>';
        priceRow.innerHTML = '<td class="px-4 py-2 bg-gray-100 font-semibold">가격</td>';
        linkRow.innerHTML = '<td class="px-4 py-2 bg-gray-100 font-semibold">링크</td>';
        addRow.innerHTML = '<td class="px-4 py-2 bg-gray-100 font-semibold">추가</td>'; // "추가" 열 추가

        // 선택된 각 상품에 대해 테이블에 데이터 추가
        selectedProducts.forEach(product => {
            const parser = new DOMParser();
            const parsedTitle = parser.parseFromString(product.title, 'text/html').body.textContent || "";
            const productId = product.productId || product.id || 'unknown-id';

            // 헤더에 상품 이름 추가
            const headerCell = document.createElement('th');
            headerCell.className = 'px-4 py-2';
            headerCell.textContent = parsedTitle;
            comparisonHeader.appendChild(headerCell);

            // 이미지 셀 추가 (가운데 정렬)
            const imgCell = document.createElement('td');
            imgCell.className = 'px-4 py-2';
            imgCell.innerHTML = `<div class="flex justify-center"><img src="${product.image}" alt="${parsedTitle}" class="w-24 h-24 object-cover"></div>`;
            imgRow.appendChild(imgCell);

            // 이름 셀 추가
            const nameCell = document.createElement('td');
            nameCell.className = 'px-4 py-2';
            nameCell.textContent = parsedTitle;
            nameRow.appendChild(nameCell);

            // 가격 셀 추가
            const priceCell = document.createElement('td');
            priceCell.className = 'px-4 py-2';
            priceCell.textContent = `${new Intl.NumberFormat().format(product.price)} 원`;
            priceRow.appendChild(priceCell);

            // 링크 셀 추가
            const linkCell = document.createElement('td');
            linkCell.className = 'px-4 py-2';
            linkCell.innerHTML = `<a href="${product.link}" target="_blank" class="text-blue-500 hover:underline">구매 링크</a>`;
            linkRow.appendChild(linkCell);

            // 추가 셀에 위시리스트 추가 버튼 추가
            const addCell = document.createElement('td');
            addCell.className = 'px-4 py-2';

            addCell.innerHTML = `
                <button type="button" class="bg-green-500 text-white mt-2 px-4 py-2 rounded hover:bg-green-600 focus:outline-none wishlist-modal-btn" 
                    data-id="${productId}" data-title="${parsedTitle}" data-image="${product.image}" data-price="${product.price}" data-link="${product.link}">
                    위시리스트에 추가
                </button>`;
            addRow.appendChild(addCell);
        });

        // 테이블 바디에 행 추가
        comparisonTableBody.appendChild(imgRow);
        comparisonTableBody.appendChild(nameRow);
        comparisonTableBody.appendChild(priceRow);
        comparisonTableBody.appendChild(linkRow);
        comparisonTableBody.appendChild(addRow); // 추가 행도 테이블에 추가

        // 모달창 표시 (showModal이 true일 때만)
        if (showModal && selectedProducts.length > 0) {
            document.getElementById('compare-modal').classList.remove('hidden');
        }

        // "위시리스트에 추가" 버튼 클릭 시 위시리스트 모달 표시
        document.querySelectorAll('.wishlist-modal-btn').forEach(button => {
            button.addEventListener('click', (event) => {
                const productData = {
                    productId: event.currentTarget.getAttribute('data-id'),
                    title: event.currentTarget.getAttribute('data-title'),
                    lprice: event.currentTarget.getAttribute('data-price'),
                    link: event.currentTarget.getAttribute('data-link'),
                    image: event.currentTarget.getAttribute('data-image')
                };

                // 위시리스트 모달에 JSON 데이터 설정
                const wishlistModal = document.getElementById('wishlist-modal');
                wishlistModal.dataset.productData = JSON.stringify(productData);
                wishlistModal.classList.remove('hidden');

                wishlistModal.style.zIndex = '1000';
            });
        });
    }

    // 모달창 닫기 기능 추가
    document.getElementById('close-compare-modal').addEventListener('click', () => {
        document.getElementById('compare-modal').classList.add('hidden');
    });

    // 모달창 닫기 함수
    function closeModal() {
        document.getElementById('wishlist-modal').classList.add('hidden');
        const privateButton = document.getElementById('default-checkbox');
        privateButton.checked = true;
        temp = "private";
    }

    // 모달창 내 버튼 이벤트 핸들러 설정
    document.getElementById('close-modal').addEventListener('click', closeModal);
    document.getElementById('cancel-wishlist').addEventListener('click', closeModal);
    document.getElementById('wishlist-form').addEventListener('submit', (event) => {
        event.preventDefault(); // 폼 제출 기본 동작 막기

        // 모달창에 저장된 JSON 데이터 가져오기
        let productData = JSON.parse(document.getElementById('wishlist-modal').dataset.productData);
        productData.state = temp;
        console.log("모달창 데이터: ", productData);

        // 카테고리 ID 가져오기
        const categoryId = document.getElementById('my_list').value;
        productData.categoryId = categoryId;

        // 동일한 상품이 있는지 서버에 확인 요청
        fetch(`${contextPath}/wishlist/category/${categoryId}/products/check-duplicate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ productId: productData.productId })
        })
            .then(response => {
                if (response.status === 409) {
                    alert('동일 상품이 있습니다!');
                    throw new Error('This product already exists in the selected category.');
                }
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.statusText);
                }
                return response.json();
            })
            .then(() => {
                // 위시리스트에 추가 요청
                return fetch(`${contextPath}/wishlist/category/${categoryId}/products`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(productData)
                });
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                console.log('Product added to wishlist:', data);
                alert('위시리스트에 추가되었습니다!');
                closeModal();
            })
            .catch(error => {
                console.error('Error:', error);
                if (error.message !== 'This product already exists in the selected category.') {
                    alert(error.message);
                }
                closeModal();
            });
    });

    // 페이지 로드 시 디폴트 키워드로 상품 목록을 불러오기
    fetchAndRenderProducts();
});
