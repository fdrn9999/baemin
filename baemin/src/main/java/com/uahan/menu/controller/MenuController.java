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
import java.io.PrintWriter;
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
            // Extract search and filter parameters
            String searchQuery = req.getParameter("searchQuery");
            String categoryCodeStr = req.getParameter("categoryCode");
            String excludeSoldOutStr = req.getParameter("excludeSoldOut");
            String nameSort = req.getParameter("nameSort");
            String priceSort = req.getParameter("priceSort");

            // Parse categoryCode to Integer
            Integer categoryCode = null;
            if (categoryCodeStr != null && !categoryCodeStr.trim().isEmpty()) {
                try {
                    categoryCode = Integer.parseInt(categoryCodeStr);
                } catch (NumberFormatException e) {
                    // Invalid category code, ignore
                }
            }

            // Parse excludeSoldOut to boolean
            boolean excludeSoldOut = Boolean.parseBoolean(excludeSoldOutStr);

            // Use new search method that handles filtering
            List<MenuDTO> menuList = menuService.searchMenus(searchQuery, categoryCode, excludeSoldOut, nameSort,
                    priceSort);
            List<CategoryDTO> categoryList = menuService.selectAllCategories();

            req.setAttribute("menuList", menuList);
            req.setAttribute("categoryList", categoryList);

            String ajaxHeader = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(ajaxHeader)) {
                req.getRequestDispatcher("/WEB-INF/views/menu/list_content.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/views/menu/list.jsp").forward(req, resp);
            }
        } else {
            // All other get requests (regist, update, detail) redirect to list as we use
            // modals now
            resp.sendRedirect(req.getContextPath() + "/menu/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain;charset=UTF-8"); // Response for AJAX

        PrintWriter out = resp.getWriter();
        int result = 0;

        try {
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

                result = menuService.registMenu(menu);

            } else if ("/update".equals(pathInfo)) {
                int code = Integer.parseInt(req.getParameter("menuCode"));
                String name = req.getParameter("menuName");
                int price = Integer.parseInt(req.getParameter("menuPrice"));
                int categoryCode = Integer.parseInt(req.getParameter("categoryCode"));
                String status = req.getParameter("orderableStatus");

                MenuDTO menu = new MenuDTO(code, name, price, categoryCode, status);

                result = menuService.modifyMenu(menu);

            } else if ("/delete".equals(pathInfo)) {
                int code = Integer.parseInt(req.getParameter("menuCode"));
                result = menuService.deleteMenu(code);
            }

            if (result > 0) {
                out.print("success");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("error");
        }
    }
}
