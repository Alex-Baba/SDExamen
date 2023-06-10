package Servlets;

import Database.StudentCRUD;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StudentCRUD operations = new StudentCRUD();
        try {
            operations.deleteStudent(Integer.valueOf(req.getParameter("id")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.getRequestDispatcher("./index.jsp").forward(req, resp);
    }
}
