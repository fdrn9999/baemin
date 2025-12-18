package com.uahan.menu.controller;

import com.uahan.menu.model.dto.MenuDTO;
import com.uahan.menu.model.dto.CategoryDTO;
import com.uahan.menu.model.service.MenuService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/menu/*")
public class MenuController extends HttpServlet {

    private MenuService menuService;

    @Override
    public void init() throws ServletException {
        menuService = new MenuService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/list".equals(pathInfo)) {
            String sort = req.getParameter("sort");
            List<MenuDTO> menuList = menuService.selectAllMenus(sort);
            List<CategoryDTO> categoryList = menuService.selectAllCategories(); // Fetch categories for modal

            req.setAttribute("menuList", menuList);
            req.setAttribute("categoryList", categoryList);

            String ajaxHeader = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(ajaxHeader)) {
                req.getRequestDispatcher("/WEB-INF/views/menu/list_content.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/views/menu/list.jsp").forward(req, resp);
            }
        } else if ("/detail".equals(pathInfo)) {
            resp.sendRedirect(req.getContextPath() + "/menu/list");
        } else if ("/regist".equals(pathInfo)) {
            List<CategoryDTO> categoryList = menuService.selectAllCategories();
            req.setAttribute("categoryList", categoryList);
            req.getRequestDispatcher("/WEB-INF/views/menu/regist.jsp").forward(req, resp);
        } else if ("/update".equals(pathInfo)) {
            int menuCode = Integer.parseInt(req.getParameter("menuCode"));
            MenuDTO menu = menuService.selectMenuById(menuCode);
            List<CategoryDTO> categoryList = menuService.selectAllCategories();
            req.setAttribute("menu", menu);
            req.setAttribute("categoryList", categoryList);
            req.getRequestDispatcher("/WEB-INF/views/menu/regist.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");

        if ("/regist".equals(pathInfo)) {
            String name = req.getParameter("menuName");
            int price = Integer.parseInt(req.getParameter("menuPrice"));
            int categoryCode = Integer.parseInt(req.getParameter("categoryCode"));
            String status = req.getParameter("orderableStatus");

            MenuDTO menu = new MenuDTO();
            menu.setMenuName(name);
            menu.setMenuPrice(price);
            menu.setCategoryCode(categoryCode);
            menu.setOrderableStatus(status);

            int result = menuService.registMenu(menu);

            if (result > 0) {
                resp.sendRedirect(req.getContextPath() + "/menu/list");
            } else {
                req.setAttribute("message", "메뉴 등록 실패");
                req.getRequestDispatcher("/WEB-INF/views/common/error.jsp").forward(req, resp);
            }

        } else if ("/update".equals(pathInfo)) {
            int code = Integer.parseInt(req.getParameter("menuCode"));
            String name = req.getParameter("menuName");
            int price = Integer.parseInt(req.getParameter("menuPrice"));
            int categoryCode = Integer.parseInt(req.getParameter("categoryCode"));
            String status = req.getParameter("orderableStatus");

            MenuDTO menu = new MenuDTO(code, name, price, categoryCode, status);

            int result = menuService.modifyMenu(menu);

            if (result > 0) {
                resp.sendRedirect(req.getContextPath() + "/menu/list");
            } else {
                req.setAttribute("message", "메뉴 수정 실패");
                req.getRequestDispatcher("/WEB-INF/views/common/error.jsp").forward(req, resp);
            }
        } else if ("/delete".equals(pathInfo)) {
            int code = Integer.parseInt(req.getParameter("menuCode"));
            int result = menuService.deleteMenu(code);

            if (result > 0) {
                resp.sendRedirect(req.getContextPath() + "/menu/list");
            } else {
                req.setAttribute("message", "메뉴 삭제 실패");
                req.getRequestDispatcher("/WEB-INF/views/common/error.jsp").forward(req, resp);
            }
        }
    }
}
