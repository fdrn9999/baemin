<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <c:forEach var="menu" items="${menuList}">
            <div class="menu-card" data-name="${menu.menuName}" data-price="${menu.menuPrice}"
                onclick="openModal('${menu.menuCode}', '${menu.menuName}', '${menu.menuPrice}', '${menu.categoryName}', '${menu.categoryCode}', '${menu.orderableStatus}')">
                <div class="menu-icon">🍽️</div>
                <div class="menu-info">
                    <div class="menu-name">
                        ${menu.menuName}
                        <span class="menu-status ${menu.orderableStatus == 'Y' ? 'status-y' : 'status-n'}">
                            ${menu.orderableStatus == 'Y' ? '주문가능' : '품절'}
                        </span>
                    </div>
                    <div class="menu-price">${menu.menuPrice}원</div>
                    <div style="font-size: 0.8rem; color: #888; margin-top: 5px;">${menu.categoryName}</div>
                </div>
            </div>
        </c:forEach>