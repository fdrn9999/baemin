package com.uahan.menu.model.service;

import com.uahan.common.JDBCTemplate;
import com.uahan.menu.model.dao.MenuDAO;
import com.uahan.menu.model.dto.CategoryDTO;
import com.uahan.menu.model.dto.MenuDTO;

import java.sql.Connection;
import java.util.List;

public class MenuService {

    private final MenuDAO menuDAO;

    public MenuService() {
        menuDAO = new MenuDAO();
    }

    public List<MenuDTO> selectAllMenus(String sort) {
        Connection con = JDBCTemplate.getConnection();
        List<MenuDTO> menuList = menuDAO.selectAllMenus(con, sort);
        JDBCTemplate.close(con);
        return menuList;
    }

    public List<MenuDTO> searchMenus(String searchQuery, Integer categoryCode, boolean excludeSoldOut, String nameSort,
            String priceSort) {
        Connection con = JDBCTemplate.getConnection();
        List<MenuDTO> menuList = menuDAO.selectMenusWithFilter(con, searchQuery, categoryCode, excludeSoldOut, nameSort,
                priceSort);
        JDBCTemplate.close(con);
        return menuList;
    }

    public MenuDTO selectMenuById(int menuCode) {
        Connection con = JDBCTemplate.getConnection();
        MenuDTO menu = menuDAO.selectMenuById(con, menuCode);
        JDBCTemplate.close(con);
        return menu;
    }

    public int registMenu(MenuDTO menu) {
        Connection con = JDBCTemplate.getConnection();
        int result = menuDAO.insertMenu(con, menu);

        if (result > 0) {
            JDBCTemplate.commit(con);
        } else {
            JDBCTemplate.rollback(con);
        }
        JDBCTemplate.close(con);

        return result;
    }

    public int modifyMenu(MenuDTO menu) {
        Connection con = JDBCTemplate.getConnection();
        int result = menuDAO.updateMenu(con, menu);

        if (result > 0) {
            JDBCTemplate.commit(con);
        } else {
            JDBCTemplate.rollback(con);
        }
        JDBCTemplate.close(con);

        return result;
    }

    public int deleteMenu(int menuCode) {
        Connection con = JDBCTemplate.getConnection();
        int result = menuDAO.deleteMenu(con, menuCode);

        if (result > 0) {
            JDBCTemplate.commit(con);
        } else {
            JDBCTemplate.rollback(con);
        }
        JDBCTemplate.close(con);

        return result;
    }

    public List<CategoryDTO> selectAllCategories() {
        Connection con = JDBCTemplate.getConnection();
        List<CategoryDTO> categoryList = menuDAO.selectAllCategories(con);
        JDBCTemplate.close(con);
        return categoryList;
    }
}
