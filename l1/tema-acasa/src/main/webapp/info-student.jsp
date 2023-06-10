<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Informatii student</title>
    </head>
    <body>
        <h3>Informatii student</h3>
        <!-- populare bean cu informatii din cererea HTTP -->
        <jsp:useBean id="studentBean" class="beans.StudentBean" />
        <jsp:setProperty name="studentBean" property="id" value='<%=request.getAttribute("id") %>'/>
        <jsp:setProperty name="studentBean" property="nume" value='<%=request.getAttribute("nume") %>'/>
        <jsp:setProperty name="studentBean" property="prenume" value='<%=request.getAttribute("prenume") %>'/>
        <jsp:setProperty name="studentBean" property="varsta" value='<%=request.getAttribute("varsta") %>'/>
        <jsp:setProperty name="studentBean" property="grupa" value='<%=request.getAttribute("grupa") %>'/>
        <jsp:setProperty name="studentBean" property="medie" value='<%=request.getAttribute("medie") %>'/>
        <jsp:setProperty name="studentBean" property="credite" value='<%=request.getAttribute("credite") %>'/>

        <!-- folosirea bean-ului pentru afisarea informatiilor -->
        <p>Urmatoarele informatii au fost introduse:</p>
        <ul type="bullet">
            <li>Nume: <jsp:getProperty name="studentBean" property="nume" /></li>
            <li>Prenume: <jsp:getProperty name="studentBean" property="prenume" /></li>
            <li>Varsta: <jsp:getProperty name="studentBean" property="varsta" /></li>
            <li>Grupa: <jsp:getProperty name="studentBean" property="grupa" /></li>
            <li>Medie: <jsp:getProperty name="studentBean" property="medie" /></li>
            <li>Credite: <jsp:getProperty name="studentBean" property="credite" /></li>
        </ul>
        <form action="./edit-student" method=get>
            <input type="hidden" name="id" value='<%=request.getAttribute("id") %>' />
            <input type="submit" value="Actualizeaza" />
        </form>
        <form action="./delete-student" method=get>
            <input type="hidden" name="id" value='<%=request.getAttribute("id") %>' />
            <input type="submit" value="Sterge" />
        </form>
        <form action="./index.jsp">
            <button type="submit" name="submit">Inapoi la prima pagina</button>
        </form>
    </body>
</html>