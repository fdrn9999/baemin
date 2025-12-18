<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <html>

        <head>
            <title>배달의 민족 - 메뉴 관리</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">

            <%-- Fallback Icon Logic (Inline for simplicity or move to backend completely) --%>
                <%-- Using JSTL Logic in logic bean is better, but here we assume icon is handled effectively or use
                    simple map --%>
        </head>

        <body>

            <div class="container">
                <header>
                    <a href="${pageContext.request.contextPath}/" class="header-btn">‹ 홈</a>
                    <h1>메뉴 관리</h1>
                    <div style="width: 30px;"></div> <!-- Spacer -->
                </header>

                <div class="sort-controls">
                    <button class="sort-btn" id="sortName" onclick="toggleSort('name')">이름순</button>
                    <button class="sort-btn" id="sortPrice" onclick="toggleSort('price')">가격순</button>
                </div>

                <div class="menu-list" id="menuListContainer">
                    <jsp:include page="list_content.jsp" />
                </div>

                <!-- Floating Action Button for Registration -->
                <a href="${pageContext.request.contextPath}/menu/regist" class="fab">+</a>

                <footer class="site-footer">
                    <div class="footer-content">
                        <p class="footer-team">1팀 | 팀원: 정진호 윤성원 정병진 최현지</p>
                        <p class="footer-copy">Copyright © Baemin Corp. All rights reserved.</p>
                    </div>
                </footer>
            </div>

            <!-- Modal -->
            <div id="menuModal" class="modal-overlay">
                <div class="modal-content">
                    <button class="btn-close" onclick="closeModal()">×</button>
                    <h2 class="modal-title" id="modalTitle">메뉴 상세</h2>

                    <div class="modal-body">
                        <p><span class="modal-label">메뉴명</span> <span class="modal-value" id="modalName"></span></p>
                        <p><span class="modal-label">가격</span> <span class="modal-value" id="modalPrice"></span></p>
                        <p><span class="modal-label">카테고리</span> <span class="modal-value" id="modalCategory"></span>
                        </p>
                        <p><span class="modal-label">상태</span> <span class="modal-value" id="modalStatus"></span></p>
                    </div>

                    <div class="modal-actions">
                        <button class="btn btn-primary" id="btnUpdate">수정</button>
                        <button class="btn btn-danger" onclick="deleteMenu()">삭제</button>
                    </div>

                    <form id="deleteForm" action="${pageContext.request.contextPath}/menu/delete" method="post"
                        style="display:none;">
                        <input type="hidden" name="menuCode" id="deleteMenuCode">
                    </form>
                </div>
            </div>

            <!-- Delete Confirmation Modal -->
            <div id="deleteModal" class="modal-overlay delete-modal">
                <div class="modal-content">
                    <button class="btn-close" onclick="closeDeleteModal()">×</button>
                    <h2 class="modal-title">메뉴 삭제</h2>

                    <div class="modal-body" style="text-align: center; display: block;">
                        <p style="justify-content: center; margin-bottom: 20px;">정말 삭제하시겠습니까?<br>삭제된 메뉴는 복구할 수 없습니다.</p>
                    </div>

                    <div class="modal-actions">
                        <button class="btn btn-secondary" onclick="closeDeleteModal()">취소</button>
                        <button class="btn btn-primary" onclick="confirmDelete()">삭제하기</button>
                    </div>
                </div>
            </div>

            <!-- Update Modal -->
            <div id="updateModal" class="modal-overlay">
                <div class="modal-content">
                    <button class="btn-close" onclick="closeUpdateModal()">×</button>
                    <h2 class="modal-title">메뉴 수정</h2>

                    <form action="${pageContext.request.contextPath}/menu/update" method="post">
                        <input type="hidden" name="menuCode" id="updateMenuCode">

                        <div class="form-group">
                            <label class="form-label">메뉴명</label>
                            <input type="text" name="menuName" id="updateMenuName" class="form-input" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label">가격</label>
                            <input type="number" name="menuPrice" id="updateMenuPrice" class="form-input" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label">카테고리</label>
                            <select name="categoryCode" id="updateCategoryCode" class="form-select">
                                <c:forEach var="category" items="${categoryList}">
                                    <option value="${category.categoryCode}">${category.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">판매 상태</label>
                            <select name="orderableStatus" id="updateOrderableStatus" class="form-select">
                                <option value="Y">주문 가능</option>
                                <option value="N">품절</option>
                            </select>
                        </div>

                        <div class="modal-actions">
                            <button type="button" class="btn btn-secondary" onclick="closeUpdateModal()">취소</button>
                            <button type="submit" class="btn btn-primary">수정 완료</button>
                        </div>
                    </form>
                </div>
            </div>

            <script>
                const modal = document.getElementById('menuModal');
                const deleteModal = document.getElementById('deleteModal');
                const updateModal = document.getElementById('updateModal');
                const deleteInput = document.getElementById('deleteMenuCode');
                const updateBtn = document.getElementById('btnUpdate');

                // Update Form Elements
                const upCode = document.getElementById('updateMenuCode');
                const upName = document.getElementById('updateMenuName');
                const upPrice = document.getElementById('updateMenuPrice');
                const upCategory = document.getElementById('updateCategoryCode');
                const upStatus = document.getElementById('updateOrderableStatus');

                // Sorting State: Server-side driven via AJAX
                let currentSortState = ''; // Store client-side to manage toggle logic

                function toggleSort(type) {
                    let newSort = '';

                    // Logic to determine next sort state
                    if (type === 'name') {
                        if (currentSortState === 'name_asc') {
                            newSort = 'name_desc';
                        } else if (currentSortState === 'name_desc') {
                            newSort = '';
                        } else {
                            newSort = 'name_asc';
                        }
                    } else if (type === 'price') {
                        if (currentSortState === 'price_asc') {
                            newSort = 'price_desc';
                        } else if (currentSortState === 'price_desc') {
                            newSort = '';
                        } else {
                            newSort = 'price_asc';
                        }
                    }

                    // AJAX Request
                    fetch('${pageContext.request.contextPath}/menu/list?sort=' + newSort, {
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest'
                        }
                    })
                        .then(response => response.text())
                        .then(html => {
                            document.getElementById('menuListContainer').innerHTML = html;
                            currentSortState = newSort;
                            updateSortButtons(newSort);

                            // Update URL without reload (History API)
                            const newUrl = new URL(window.location);
                            if (newSort) {
                                newUrl.searchParams.set('sort', newSort);
                            } else {
                                newUrl.searchParams.delete('sort');
                            }
                            window.history.pushState({}, '', newUrl);
                        })
                        .catch(err => console.error('Sort failed', err));
                }

                function updateSortButtons(sort) {
                    // Reset all
                    document.getElementById('sortName').className = 'sort-btn';
                    document.getElementById('sortPrice').className = 'sort-btn';

                    if (!sort) return;

                    if (sort.startsWith('name')) {
                        const btn = document.getElementById('sortName');
                        btn.className = 'sort-btn active';
                        if (sort.endsWith('asc')) btn.classList.add('asc');
                        if (sort.endsWith('desc')) btn.classList.add('desc');
                    } else if (sort.startsWith('price')) {
                        const btn = document.getElementById('sortPrice');
                        btn.className = 'sort-btn active';
                        if (sort.endsWith('asc')) btn.classList.add('asc');
                        if (sort.endsWith('desc')) btn.classList.add('desc');
                    }
                }

                // Initialize State on Load
                window.onload = function () {
                    const urlParams = new URLSearchParams(window.location.search);
                    currentSortState = urlParams.get('sort') || '';
                    updateSortButtons(currentSortState);
                };

                // Open Detail Modal
                function openModal(code, name, price, categoryName, categoryCode, status) {
                    document.getElementById('modalName').innerText = name;
                    document.getElementById('modalPrice').innerText = price + '원';
                    document.getElementById('modalCategory').innerText = categoryName;
                    document.getElementById('modalStatus').innerText = status === 'Y' ? '주문가능' : '품절';

                    deleteInput.value = code;

                    // Setup Update Button to open Update Modal
                    updateBtn.onclick = function () {
                        openUpdateModal(code, name, price, categoryCode, status);
                    };

                    modal.style.display = 'flex';
                }

                function closeModal() {
                    modal.style.display = 'none';
                }

                function openDeleteModal() {
                    deleteModal.style.display = 'flex';
                    closeModal();
                }

                function closeDeleteModal() {
                    deleteModal.style.display = 'none';
                    modal.style.display = 'flex';
                }

                function openUpdateModal(code, name, price, categoryCode, status) {
                    upCode.value = code;
                    upName.value = name;
                    upPrice.value = price;
                    upCategory.value = categoryCode;
                    upStatus.value = status;

                    updateModal.style.display = 'flex';
                    closeModal(); // Hide detail
                }

                function closeUpdateModal() {
                    updateModal.style.display = 'none';
                    modal.style.display = 'flex'; // Back to detail
                }

                function deleteMenu() {
                    openDeleteModal();
                }

                function confirmDelete() {
                    document.getElementById("deleteForm").submit();
                }

                window.onclick = function (event) {
                    if (event.target == modal) closeModal();
                    if (event.target == deleteModal) closeDeleteModal();
                    if (event.target == updateModal) closeUpdateModal();
                }

            </script>
        </body>

        </html>