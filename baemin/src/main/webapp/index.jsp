<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>

    <head>
        <title>배달의 민족 - 사장님 광장</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
        <style>
            .landing-container {
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                height: 80vh;
                text-align: center;
            }

            .logo {
                font-size: 3rem;
                font-weight: 800;
                color: var(--baemin-mint);
                margin-bottom: 20px;
                text-shadow: 2px 2px 0px #eee;
            }

            .subtitle {
                font-size: 1.2rem;
                color: #666;
                margin-bottom: 40px;
            }

            .btn-start {
                background-color: var(--baemin-mint);
                color: white;
                padding: 15px 40px;
                font-size: 1.2rem;
                border-radius: 50px;
                font-weight: bold;
                box-shadow: 0 4px 15px rgba(42, 193, 188, 0.4);
                transition: transform 0.2s;
            }

            .btn-start:hover {
                transform: scale(1.05);
            }
        </style>
    </head>

    <body>

        <div class="container">
            <div class="landing-container">
                <div class="logo">배달의 민족<br>사장님 광장</div>
                <p class="subtitle">우리 가게 메뉴를 쉽고 간편하게 관리하세요</p>

                <a href="menu/list" class="btn-start">메뉴 관리 시작하기</a>
            </div>

            <footer class="site-footer">
                <div class="footer-content">
                    <p class="footer-team">1팀 | 팀원: 정진호 윤성원 정병진 최현지</p>
                    <p class="footer-copy">Copyright © Baemin Corp. All rights reserved.</p>
                </div>
            </footer>
        </div>

    </body>

    </html>