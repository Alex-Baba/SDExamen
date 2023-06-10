package Servlets;

import Database.StudentCRUD;
import beans.StudentBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        StudentCRUD crud = new StudentCRUD();
        List<StudentBean> list = null;
        try {
            list = crud.getAllStudents();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(list);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(jsonString);
        resp.getWriter().flush();
    }
}
