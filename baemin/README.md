# 🛵 배달의 민족 사장님 광장 - 팀원(비전공자)을 위한 코드 가이드

<div align="center">
  <img src="https://img.shields.io/badge/Project-Baemin_Menu_System-2AC1BC?style=for-the-badge&logo=baemin&logoColor=white">
  <br/>
  <h3>"자바? 서블릿? 이게 다 뭔가요?"</h3>
  <p>이 문서는 코딩이 낯선 팀원들이 프로젝트의 <b>모든 코드</b>를 한 줄도 빠짐없이 이해할 수 있도록 작성된 <b>친절한 해설서</b>입니다.<br>
  Github 메인화면(README)에서 바로 읽으시면 됩니다.</p>
</div>

---

## 📚 목차
1.  **시작하기 전에: 필수 개념 (아주 쉬운 설명)**
2.  **프로젝트 전체 구조**
3.  **데이터 흐름 (주문에서 배달까지)**
4.  **소스 코드 전체 보기 및 해설**
    *   [1. 공용 도구 (JDBCTemplate)](#1-공용-도구-jdbctemplatejava)
    *   [2. 데이터 모델 (DTO)](#2-데이터-모델-dto---배달-가방)
    *   [3. 쿼리 저장소 (XML)](#3-쿼리-저장소-xml---레시피북)
    *   [4. 데이터 접근 (DAO)](#4-데이터-접근-dao---창고-관리자)
    *   [5. 비즈니스 로직 (Service)](#5-비즈니스-로직-service---지배인)
    *   [6. 컨트롤러 (Controller)](#6-컨트롤러-controller---카운터)
    *   [7. 화면 (View - JSP)](#7-화면-view---jsp)
    *   [8. 메인 화면 (index.jsp)](#8-메인-화면-indexjsp)

---

## 1. 💡 시작하기 전에: 필수 개념 (이건 알고 가자!)

코드를 보기 전에, **"이게 왜 필요한지"** 모르면 외계어처럼 보입니다. 딱 5가지만 짚고 넘어갑시다.

### ① JDBC (자바와 DB의 연결고리)
*   자바는 영어를 쓰고, DB는 스페인어(SQL)를 씁니다. 말이 안 통하죠?
*   이 둘 사이를 연결해서 통역해주는 기술이 **JDBC**입니다.
*   "전화 걸기(Connection)", "말하기(Statement)", "듣기(ResultSet)" 기능을 합니다.

### ② 서블릿 (Servlet) & 톰캣 (Tomcat)
*   **톰캣**: 우리가 만든 웹 사이트를 24시간 띄워주는 **가게 건물(서버)**입니다.
*   **서블릿**: 가게에서 일하는 **점원(자바 파일)**입니다. 손님이 오면 주문을 받고, 주방에 오더를 넣습니다.

### ③ 트랜잭션 (Transaction - "한 큐에 끝내기")
*   은행 이체를 생각해보세요. 내 통장에서 돈은 나갔는데, 상대방 통장에 안 들어갔다면? 큰일나죠.
*   **"모든 과정이 성공해야 저장(Commit), 하나라도 실패하면 전체 취소(Rollback)"**
*   이걸 관리하는 게 이 프로젝트의 핵심입니다. (`Service`가 담당)

### ④ DTO, DAO, Service, Controller (역할 놀이)
*   **DTO (가방)**: 그냥 데이터 담는 그릇. 로직 없음.
*   **DAO (창고지기)**: DB에서 데이터 꺼내고 넣는 일만 함.
*   **Service (지배인)**: 전체적인 일 처리를 지시함. (주문 받고 -> 재고 확인 -> 결제 지시)
*   **Controller (카운터)**: 손님(화면)과 처음 만나는 곳.

### ⑤ static (공유재)
*   `static`이 붙은 메서드나 변수는 **"객체를 생성(new)하지 않아도 쓸 수 있다"**는 뜻입니다.
*   `JDBCTemplate.getConnection()` 처럼 씁니다. (편리함!)

---

## 2. 🏗️ 프로젝트 전체 구조

```text
src/main
├── java/com/uahan                 
│   ├── common/                    
│   │   └── JDBCTemplate.java      (🔌 DB 연결 도구)
│   └── menu/                      
│       ├── controller/
│       │   └── MenuController.java (🚥 요청 처리반)
│       ├── model/
│       │   ├── dto/                (🍱 데이터 가방)
│       │   │   ├── MenuDTO.java
│       │   │   └── CategoryDTO.java
│       │   ├── dao/                (🛠️ 창고 관리자)
│       │   │   └── MenuDAO.java
│       │   └── service/            (👔 지배인)
│       │       └── MenuService.java
├── resources/                     
│   └── mapper/
│       └── menu-query.xml         (📜 SQL 모음집)
└── webapp/
    ├── index.jsp                  (🏠 메인 대문)
    └── WEB-INF/views/             (🖼️ 보안 화면 파일들)
        ├── menu/
        │   ├── list.jsp
        │   └── regist.jsp
        └── common/
            └── error.jsp
```

---

## 3. 🚀 데이터 흐름 (주문에서 배달까지)

**"메뉴 저장 버튼을 눌렀을 때 무슨 일이 일어나나요?"**

1.  **[화면]**: 사용자가 입력한 데이터(치킨, 2만원)가 `MenuController`로 날아갑니다.
2.  **[Controller]**: 데이터를 `MenuDTO`(가방)에 담아서 `Service`를 부릅니다.
3.  **[Service]**: `Connection`(전화)을 연결합니다. `DAO`에게 "저장해줘" 시킵니다.
    *   *왜 여기서 연결하나요?*: 트랜잭션 관리(취소/확정)를 여기서 해야 하니까요!
4.  **[DAO]**: `Connection`을 받아서 진짜 쿼리(`INSERT`)를 DB에 날립니다.
5.  **[Service]**: 성공하면 `commit`, 실패하면 `rollback`하고 전화를 끊습니다.
6.  **[Controller]**: "성공했습니다!" 하고 목록 페이지로 보냅니다.

---

## 4. 📝 소스 코드 전체 보기 및 해설

여기서부터는 **파일의 모든 내용**을 보여드리고, **한 줄 한 줄** 설명합니다. 스크롤 압박이 있어도 천천히 읽어보세요.

---

### 1. 공용 도구 (JDBCTemplate.java)
![Java](https://img.shields.io/badge/Java-JDBCTemplate.java-ED8B00?style=flat&logo=semver&logoColor=white)

매번 DB 연결 코드를 짜는 건 귀찮고 실수하기 쉽습니다. 그래서 **"연결(getConnection)", "닫기(close)", "확정(commit)", "취소(rollback)"** 기능을 미리 만들어두고 갖다 쓰는 파일입니다.

```java
package com.uahan.common;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JDBCTemplate {

    // 1. DB 연결을 가져오는 메서드
    // static이라서 'new JDBCTemplate()' 없이 바로 쓸 수 있습니다.
    public static Connection getConnection() {
        Properties prop = new Properties(); // 설정값을 읽기 위한 도구
        Connection con = null; // 연결 객체 (처음엔 비어있음)
        
        try {
            // (1) 설정 파일 읽기: resources 폴더에 있는 파일을 찾아서 읽습니다.
            // 여기에 DB 주소, 아이디, 비번이 적혀있습니다.
            prop.load(JDBCTemplate.class.getClassLoader().getResourceAsStream("jdbc-config.properties"));
            
            String url = prop.getProperty("url");
            String user = prop.getProperty("user");
            String password = prop.getProperty("password");

            // (2) 드라이버 등록: "나 MySQL 쓸 거야"라고 자바에 알립니다.
            // 이게 없으면 연결을 못 합니다.
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // (3) 연결 시도: DriverManager라는 애가 드라이버를 이용해서 실제 연결을 만듭니다.
            con = DriverManager.getConnection(url, user, password);

            // (4) 자동 커밋 끄기: *매우 중요*
            // 기본적으로는 SQL 한 줄 실행할 때마다 자동 저장(Commit)되는데,
            // 우리는 여러 작업을 묶어서(트랜잭션) 처리해야 하므로 수동으로 하겠다고 끕니다.
            con.setAutoCommit(false);

        } catch (SQLException e) { 
            e.printStackTrace(); // DB 관련 에러나면 로그 찍어라
        } catch (IOException e) { 
            e.printStackTrace(); // 파일 못 읽으면 로그 찍어라
        } catch (ClassNotFoundException e) { 
            e.printStackTrace(); // 드라이버 없으면 로그 찍어라
        }
        return con; // 만든 연결(전화기)을 반환
    }

    // 2. 연결 닫기 (close)
    // 다 쓴 연결을 안 끊으면 계속 쌓여서 서버가 터집니다. (메모리 누수)
    public static void close(Connection con) {
        try {
            // null이 아니고(연결 자체가 없던게 아니고), !isClosed(아직 안 닫혔으면)
            if (con != null && !con.isClosed()) {
                con.close(); // 닫아라
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Statement는 쿼리를 실어나르는 트럭입니다. 얘도 닫아야 합니다.
    public static void close(Statement stmt) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ResultSet은 쿼리 결과를 담은 상자입니다. 얘도 닫아야 합니다.
    public static void close(ResultSet rset) {
        try {
            if (rset != null && !rset.isClosed()) {
                rset.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 3. 확정 (commit)
    // 트랜잭션이 성공적으로 끝났을 때 "저장해!"라고 하는 것
    public static void commit(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 4. 취소 (rollback)
    // 중간에 에러나서 "없던 일로 해!"라고 하는 것
    public static void rollback(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

---

### 2. 데이터 모델 (DTO - 배달 가방)
![Java](https://img.shields.io/badge/Java-MenuDTO.java-EA5442?style=flat&logo=java&logoColor=white)

데이터를 이쪽 파일에서 저쪽 파일로 옮길 때 쓰는 **가방**입니다. 기능은 없고 변수만 있습니다.

```java
package com.uahan.menu.model.dto;

public class MenuDTO {

    // 필드: 메뉴 하나가 가지는 정보들
    // private을 쓴 이유: 남들이 변수에 바로 접근해서 이상한 값 넣을까봐 막아둠.
    private int menuCode;
    private String menuName;
    private int menuPrice;
    private int categoryCode;
    private String categoryName;
    private String orderableStatus;

    // 1. 기본 생성자
    // new MenuDTO() 라고 했을 때 호출됨. 빈 가방을 만듭니다.
    public MenuDTO() {
    }

    // 2. 매개변수 있는 생성자
    // 가방을 만들면서 내용물도 바로 채워넣고 싶을 때 씁니다.
    public MenuDTO(int menuCode, String menuName, int menuPrice, int categoryCode, String orderableStatus) {
        this.menuCode = menuCode;           // 내 가방의 menuCode = 전달받은 menuCode
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.categoryCode = categoryCode;
        this.orderableStatus = orderableStatus;
    }

    // 3. Getter / Setter
    // private으로 잠긴 변수를 꺼내거나(get), 값을 넣는(set) 유일한 구멍입니다.
    public int getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(int menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(int categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getOrderableStatus() {
        return orderableStatus;
    }

    public void setOrderableStatus(String orderableStatus) {
        this.orderableStatus = orderableStatus;
    }

    // toString: 가방 안에 뭐가 들었나 확인용 (System.out.println 찍을 때 예쁘게 나오게 함)
    @Override
    public String toString() {
        return "MenuDTO{" +
                "menuCode=" + menuCode +
                ", menuName='" + menuName + '\'' +
                ", menuPrice=" + menuPrice +
                ", categoryCode=" + categoryCode +
                ", orderableStatus='" + orderableStatus + '\'' +
                '}';
    }
}
```

---

### 3. 쿼리 저장소 (XML - 레시피북)
![XML](https://img.shields.io/badge/XML-menu--query.xml-orange?style=flat&logo=xml&logoColor=white)

자바 코드 안에 SQL(`SELECT * FROM...`)을 섞어 쓰면 지저분하니까, SQL만 따로 모아둔 파일입니다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
    <comment>Menu CRUD Queries</comment>
    
    <!-- 전체 메뉴 조회 SQL -->
    <!-- key="이름": 자바에서 이 이름으로 쿼리를 찾습니다. -->
    <entry key="selectAllMenus">
        SELECT 
               a.menu_code
             , a.menu_name
             , a.menu_price
             , a.category_code
             , b.category_name
             , a.orderable_status 
          FROM tbl_menu a
          JOIN tbl_category b ON a.category_code = b.category_code
         ORDER BY a.menu_code
    </entry>
    
    <!-- 메뉴 하나 상세 조회 SQL -->
    <entry key="selectMenuById">
        SELECT 
               a.menu_code
             , a.menu_name
             , a.menu_price
             , a.category_code
             , b.category_name
             , a.orderable_status
          FROM tbl_menu a
          JOIN tbl_category b ON a.category_code = b.category_code
         WHERE a.menu_code = ?
    </entry>
    
    <!-- 메뉴 등록 SQL -->
    <!-- 물음표(?)는 나중에 자바에서 값을 채워넣을 자리입니다. -->
    <entry key="insertMenu">
        INSERT 
          INTO tbl_menu 
        (
          menu_code
        , menu_name
        , menu_price
        , category_code
        , orderable_status
        ) 
        VALUES 
        (
          null      <!-- 코드는 자동생성(Auto Increment)이라 null -->
        , ?
        , ?
        , ?
        , ?
        )
    </entry>
    
    <!-- 메뉴 수정 SQL -->
    <entry key="updateMenu">
        UPDATE tbl_menu
           SET menu_name = ?
             , menu_price = ?
             , category_code = ?
             , orderable_status = ?
         WHERE menu_code = ?
    </entry>
    
    <!-- 메뉴 삭제 SQL -->
    <entry key="deleteMenu">
        DELETE 
          FROM tbl_menu
         WHERE menu_code = ?
    </entry>

    <!-- 카테고리 목록 조회 SQL (셀렉트 박스 만들 때 필요) -->
    <entry key="selectAllCategories">
        SELECT
               category_code
             , category_name
             , ref_category_code
          FROM tbl_category
         ORDER BY category_code
    </entry>
</properties>
```

---

### 4. 데이터 접근 (DAO - 창고 관리자)
![Java](https://img.shields.io/badge/Java-MenuDAO.java-007396?style=flat&logo=java&logoColor=white)

DB에 직접 접속해서 SQL을 날리는 유일한 담당자입니다.

```java
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

    // 생성자: 이 클래스가 시작되자마자 하는 일
    public MenuDAO() {
        try {
            // 아까 그 XML 파일(레시피북)을 읽어서 머릿속에 외웁니다.
            prop.loadFromXML(MenuDAO.class.getClassLoader().getResourceAsStream("mapper/menu-query.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 1. 전체 메뉴 조회
    public List<MenuDTO> selectAllMenus(Connection con) {
        // 사용할 도구들 미리 선언 (우편 집배원 같은 역할)
        PreparedStatement pstmt = null; 
        ResultSet rset = null; // 결과 담을 바구니
        List<MenuDTO> menuList = null; // 최종 반환할 리스트

        // XML에서 "selectAllMenus"라는 이름의 쿼리를 꺼냅니다.
        String query = prop.getProperty("selectAllMenus");

        try {
            // (1) 쿼리 준비
            pstmt = con.prepareStatement(query);
            // (2) 실행! (executeQuery: 조회용) -> 결과가 rset에 담김
            rset = pstmt.executeQuery();

            menuList = new ArrayList<>();

            // (3) 결과 하나씩 꺼내기 (next()는 다음 줄이 있으면 true)
            while (rset.next()) {
                MenuDTO menu = new MenuDTO();
                // DB에서 읽은 값을 가방(DTO)에 옮겨 담기
                menu.setMenuCode(rset.getInt("menu_code"));
                menu.setMenuName(rset.getString("menu_name"));
                menu.setMenuPrice(rset.getInt("menu_price"));
                // ...
                
                // 가방을 리스트에 추가
                menuList.add(menu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // (4) 뒷정리 (반드시 해야 함!)
            JDBCTemplate.close(rset);
            JDBCTemplate.close(pstmt);
        }

        return menuList; // 다 담은 리스트 반환
    }

    // 2. 메뉴 등록
    public int insertMenu(Connection con, MenuDTO menu) {
        PreparedStatement pstmt = null;
        int result = 0; // 몇 개가 저장됐는지 숫자 (성공하면 1)

        String query = prop.getProperty("insertMenu");

        try {
            pstmt = con.prepareStatement(query);
            
            // 물음표(?) 구멍 채우기
            // "INSERT ... VALUES (?, ?, ?, ?)" 이니까 순서대로 채워야 함
            pstmt.setString(1, menu.getMenuName());
            pstmt.setInt(2, menu.getMenuPrice());
            pstmt.setInt(3, menu.getCategoryCode());
            pstmt.setString(4, menu.getOrderableStatus());

            // 실행! (executeUpdate: 등록/수정/삭제용)
            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(pstmt);
        }

        return result; // "1개 등록됨" 반환
    }

    /* selectMenuById, updateMenu, deleteMenu 등도 위와 똑같은 구조라서 생략 없이 넣어야 하지만
       너무 길어지므로 패턴은 위와 동일하다는 점만 알면 됩니다. (실제 프로젝트엔 다 있습니다) */
}
```

---

### 5. 비즈니스 로직 (Service - 지배인)
![Java](https://img.shields.io/badge/Java-MenuService.java-2E7D32?style=flat&logo=java&logoColor=white)

여기서 제일 중요한 건 **Connection(전화기)**을 켜고 끄는 것입니다. 즉, **트랜잭션(모 아니면 도)**을 여기서 관리합니다.

```java
package com.uahan.menu.model.service;

import com.uahan.common.JDBCTemplate;
import com.uahan.menu.model.dao.MenuDAO;
import com.uahan.menu.model.dto.MenuDTO;
import java.sql.Connection;
import java.util.List;

public class MenuService {

    private final MenuDAO menuDAO;

    public MenuService() {
        menuDAO = new MenuDAO(); // 일꾼(DAO)을 미리 고용해 둡니다.
    }

    // 메뉴 전체 조회 업무
    public List<MenuDTO> selectAllMenus() {
        // (1) DB 연결 (전화기 듦)
        Connection con = JDBCTemplate.getConnection();
        
        // (2) 일꾼에게 전화기 넘겨주면서 일 시킴
        List<MenuDTO> menuList = menuDAO.selectAllMenus(con);
        
        // (3) 전화 끊기 (조회만 했으니까 커밋은 필요 없음)
        JDBCTemplate.close(con);
        
        return menuList; // 결과 반환
    }

    // 메뉴 등록 업무
    public int registMenu(MenuDTO menu) {
        // (1) DB 연결 (트랜잭션 시작!)
        Connection con = JDBCTemplate.getConnection();
        
        // (2) 일 시킴
        int result = menuDAO.insertMenu(con, menu);

        // (3) ★트랜잭션 결정★
        if (result > 0) {
            // 성공했으면 "도장 쾅! 저장해!"
            JDBCTemplate.commit(con);
        } else {
            // 실패했으면 "야 다 취소해! 없던 일로!"
            JDBCTemplate.rollback(con);
        }
        
        // (4) 전화 끊기
        JDBCTemplate.close(con);

        return result;
    }
}
```

---

### 6. 컨트롤러 (Controller - 카운터)
![Java](https://img.shields.io/badge/Java-MenuController.java-000000?style=flat&logo=java&logoColor=white)

손님의 요청을 가장 먼저 받는 곳입니다. `doGet`(조회)과 `doPost`(제출)로 나뉩니다.

```java
package com.uahan.menu.controller;

import com.uahan.menu.model.dto.MenuDTO;
import com.uahan.menu.model.service.MenuService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

// localhost:8080/menu/* 여기로 들어오는 모든 요청은 내가 받는다!
@WebServlet("/menu/*")
public class MenuController extends HttpServlet {

    private MenuService menuService;

    @Override
    public void init() throws ServletException {
        menuService = new MenuService(); // 가게 문 열 때 지배인 출근시킴
    }

    // GET 방식: 주로 "화면 보여줘" 할 때 씁니다.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // 사용자가 요청한 세부 주소 (예: /list)

        // 1. 목록 보여줘 (/list)
        if (pathInfo == null || "/list".equals(pathInfo)) {
            // 지배인(Service) 부름
            List<MenuDTO> menuList = menuService.selectAllMenus();
            
            // 받아온 메뉴판(데이터)을 'menuList'라는 이름표 붙여서 포장
            req.setAttribute("menuList", menuList);
            
            // "저기 list.jsp 테이블로 가서 보여드리세요" (화면 이동)
            req.getRequestDispatcher("/WEB-INF/views/menu/list.jsp").forward(req, resp);
        } 
        // 2. 등록 폼 보여줘 (/regist)
        else if ("/regist".equals(pathInfo)) {
            // 그냥 가면 안되고 카테고리 목록을 가져가야 함 (치킨, 한식 고를 수 있게)
            req.setAttribute("categoryList", menuService.selectAllCategories());
            req.getRequestDispatcher("/WEB-INF/views/menu/regist.jsp").forward(req, resp);
        }
    }

    // POST 방식: 폼 작성해서 "제출" 버튼 눌렀을 때 씁니다. (DB가 바뀔 때)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8"); // 한국말 깨짐 방지

        // 1. 등록 제출 (/regist)
        if ("/regist".equals(pathInfo)) {
            // HTML 폼에서 입력한 값(name="...")들 꺼내기
            String name = req.getParameter("menuName");
            int price = Integer.parseInt(req.getParameter("menuPrice"));
            int categoryCode = Integer.parseInt(req.getParameter("categoryCode"));
            String status = req.getParameter("orderableStatus");

            // 가방(DTO)에 짐 싸기
            MenuDTO menu = new MenuDTO();
            menu.setMenuName(name);
            menu.setMenuPrice(price);
            menu.setCategoryCode(categoryCode);
            menu.setOrderableStatus(status);

            // 지배인 호출!
            int result = menuService.registMenu(menu);

            // 결과 안내
            if (result > 0) {
                // 성공! 목록 페이지로 새로고침(이사) 가세요
                resp.sendRedirect(req.getContextPath() + "/menu/list");
            } else {
                // 실패! 에러 페이지 보여줘
                req.setAttribute("message", "메뉴 등록 실패");
                req.getRequestDispatcher("/WEB-INF/views/common/error.jsp").forward(req, resp);
            }
        }
    }
}
```

---

### 7. 화면 (View - JSP)
![JSP](https://img.shields.io/badge/JSP-list.jsp-007396?style=flat&logo=java&logoColor=white)

HTML과 비슷하지만 `<% %>`나 `${ }` 같은 자바 코드를 쓸 수 있는 파일입니다.

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %> <%-- JSTL 라이브러리 (반복문 쓰려고) --%>

<html>
<head>
    <title>배달의 민족 - 메뉴 관리</title>
</head>
<body>

    <div class="container">
        <h1>메뉴 관리</h1>

        <div class="menu-list">
            <%-- forEach: 메뉴 리스트만큼 반복해서 카드를 찍어냄 --%>
            <%-- items="${menuList}"가 Controller에서 보낸 그 데이터입니다! --%>
            <c:forEach var="menu" items="${menuList}">
                <div class="menu-card">
                    <%-- ${menu.menuName}: DTO의 getMenuName()을 호출해서 출력함 --%>
                    <div class="menu-name">${menu.menuName}</div>
                    <div class="menu-price">${menu.menuPrice}원</div>
                    <!-- 클릭하면 상세 수정 팝업 띄우는 기능 등 -->
                </div>
            </c:forEach>
        </div>

        <a href="menu/regist" class="btn">메뉴 등록하기</a>
    </div>

</body>
</html>
```

---

### 8. 메인 화면 (index.jsp)
![JSP](https://img.shields.io/badge/JSP-index.jsp-007396?style=flat&logo=java&logoColor=white)

사용자가 사이트에 처음 들어왔을 때 보이는 대문입니다.

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>배달의 민족 - 사장님 광장</title>
    <!-- 외부 스타일시트(CSS) 불러오기 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

    <div class="container">
        <div class="landing-container">
            <div class="logo">배달의 민족<br>사장님 광장</div>
            <p class="subtitle">우리 가게 메뉴를 쉽고 간편하게 관리하세요</p>

            <!-- '메뉴 관리 시작하기' 버튼을 누르면 '/menu/list'로 이동합니다. -->
            <a href="menu/list" class="btn-start">메뉴 관리 시작하기</a>
        </div>
    </div>

</body>
</html>
```

---

<div align="center">
  <h3>🏁 가이드 끝!</h3>
  <p>이제 이 코드들이 어떻게 돌아가는지 눈에 보이시나요?<br>
  어려운 게 있으면 언제든 다시 처음부터 읽어보세요. 화이팅입니다!</p>
</div>
