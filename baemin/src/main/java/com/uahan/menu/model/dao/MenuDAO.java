package com.uahan.menu.model.dao;

import com.uahan.common.JDBCTemplate;
import com.uahan.menu.model.dto.MenuDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MenuDAO {

    private Properties prop = new Properties();

    public MenuDAO() {
        try {
            prop.loadFromXML(MenuDAO.class.getClassLoader().getResourceAsStream("mapper/menu-query.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<MenuDTO> selectAllMenus(Connection con, String sort) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        List<MenuDTO> menuList = null;

        String queryKey = "selectAllMenus";
        if (sort != null) {
            switch (sort) {
                case "name_asc":
                    queryKey = "selectAllMenusByNameAsc";
                    break;
                case "name_desc":
                    queryKey = "selectAllMenusByNameDesc";
                    break;
                case "price_asc":
                    queryKey = "selectAllMenusByPriceAsc";
                    break;
                case "price_desc":
                    queryKey = "selectAllMenusByPriceDesc";
                    break;
                default:
                    queryKey = "selectAllMenus";
                    break;
            }
        }

        String query = prop.getProperty(queryKey);

        try {
            pstmt = con.prepareStatement(query);
            rset = pstmt.executeQuery();

            menuList = new ArrayList<>();

            while (rset.next()) {
                MenuDTO menu = new MenuDTO();
                menu.setMenuCode(rset.getInt("menu_code"));
                menu.setMenuName(rset.getString("menu_name"));
                menu.setMenuPrice(rset.getInt("menu_price"));
                menu.setCategoryCode(rset.getInt("category_code"));
                menu.setCategoryName(rset.getString("category_name"));
                menu.setOrderableStatus(rset.getString("orderable_status"));

                menuList.add(menu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(rset);
            JDBCTemplate.close(pstmt);
        }

        return menuList;
    }

    public List<MenuDTO> selectMenusWithFilter(Connection con, String searchQuery, Integer categoryCode,
            boolean excludeSoldOut, String nameSort, String priceSort) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        List<MenuDTO> menuList = null;

        String query = prop.getProperty("selectMenusWithFilter");

        try {
            pstmt = con.prepareStatement(query);

            // searchQuery parameters (duplicate for IS NULL and LIKE)
            pstmt.setString(1, searchQuery);
            pstmt.setString(2, searchQuery);

            // categoryCode parameters (duplicate for IS NULL and =)
            if (categoryCode == null) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
                pstmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, categoryCode);
                pstmt.setInt(4, categoryCode);
            }

            // excludeSoldOut parameter
            pstmt.setString(5, String.valueOf(excludeSoldOut));

            // sort parameters (Case when statements)
            pstmt.setString(6, nameSort);
            pstmt.setString(7, nameSort);
            pstmt.setString(8, priceSort);
            pstmt.setString(9, priceSort);

            rset = pstmt.executeQuery();

            menuList = new ArrayList<>();

            while (rset.next()) {
                MenuDTO menu = new MenuDTO();
                menu.setMenuCode(rset.getInt("menu_code"));
                menu.setMenuName(rset.getString("menu_name"));
                menu.setMenuPrice(rset.getInt("menu_price"));
                menu.setCategoryCode(rset.getInt("category_code"));
                menu.setCategoryName(rset.getString("category_name"));
                menu.setOrderableStatus(rset.getString("orderable_status"));

                menuList.add(menu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(rset);
            JDBCTemplate.close(pstmt);
        }

        return menuList;
    }

    public MenuDTO selectMenuById(Connection con, int menuCode) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        MenuDTO menu = null;

        String query = prop.getProperty("selectMenuById");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, menuCode);
            rset = pstmt.executeQuery();

            if (rset.next()) {
                menu = new MenuDTO();
                menu.setMenuCode(rset.getInt("menu_code"));
                menu.setMenuName(rset.getString("menu_name"));
                menu.setMenuPrice(rset.getInt("menu_price"));
                menu.setCategoryCode(rset.getInt("category_code"));
                menu.setCategoryName(rset.getString("category_name"));
                menu.setOrderableStatus(rset.getString("orderable_status"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(rset);
            JDBCTemplate.close(pstmt);
        }

        return menu;
    }

    public int insertMenu(Connection con, MenuDTO menu) {
        PreparedStatement pstmt = null;
        int result = 0;

        String query = prop.getProperty("insertMenu");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, menu.getMenuName());
            pstmt.setInt(2, menu.getMenuPrice());
            pstmt.setInt(3, menu.getCategoryCode());
            pstmt.setString(4, menu.getOrderableStatus());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(pstmt);
        }

        return result;
    }

    public int updateMenu(Connection con, MenuDTO menu) {
        PreparedStatement pstmt = null;
        int result = 0;

        String query = prop.getProperty("updateMenu");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, menu.getMenuName());
            pstmt.setInt(2, menu.getMenuPrice());
            pstmt.setInt(3, menu.getCategoryCode());
            pstmt.setString(4, menu.getOrderableStatus());
            pstmt.setInt(5, menu.getMenuCode());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(pstmt);
        }

        return result;
    }

    public int deleteMenu(Connection con, int menuCode) {
        PreparedStatement pstmt = null;
        int result = 0;

        String query = prop.getProperty("deleteMenu");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, menuCode);

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(pstmt);
        }

        return result;
    }

    public List<com.uahan.menu.model.dto.CategoryDTO> selectAllCategories(Connection con) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        List<com.uahan.menu.model.dto.CategoryDTO> categoryList = null;

        String query = prop.getProperty("selectAllCategories");

        try {
            pstmt = con.prepareStatement(query);
            rset = pstmt.executeQuery();

            categoryList = new ArrayList<>();

            while (rset.next()) {
                com.uahan.menu.model.dto.CategoryDTO category = new com.uahan.menu.model.dto.CategoryDTO();
                category.setCategoryCode(rset.getInt("category_code"));
                category.setCategoryName(rset.getString("category_name"));
                category.setRefCategoryCode(rset.getObject("ref_category_code", Integer.class));

                categoryList.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(rset);
            JDBCTemplate.close(pstmt);
        }

        return categoryList;
    }
}
