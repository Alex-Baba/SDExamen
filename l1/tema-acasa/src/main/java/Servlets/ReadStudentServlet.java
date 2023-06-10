package Servlets;

import Database.StudentCRUD;
import beans.StudentBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ReadStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        StudentCRUD operatii = new StudentCRUD();
        StudentBean bean = null;
        int id = 0;
        String nume, prenume;
        try {
            id = Integer.parseInt(request.getParameter("id"));
            bean = operatii.getStudentById(id);
        } catch (Exception e1) {
            try {
                nume = request.getParameter("nume");
                prenume = request.getParameter("prenume");
                bean = operatii.getStudentByFullName(nume,prenume);
            } catch (Exception e2) {
                response.sendError(400,"Date de cautare necorespunzatoare!");
                return;
            }
        }
        if(bean == null) {
            response.sendError(404, "Nu a fost gasit niciun student in baza de date!");
            return;
        }
        request.setAttribute("id", id);
        request.setAttribute("nume", bean.getNume());
        request.setAttribute("prenume", bean.getPrenume());
        request.setAttribute("varsta", bean.getVarsta());
        request.setAttribute("credite", bean.getCredite());
        request.setAttribute("medie", bean.getMedie());
        request.setAttribute("grupa", bean.getGrupa());
// redirectionare date catre pagina de afisare a informatiilorstudentului
        request.getRequestDispatcher("./info-student.jsp").forward(request, response);
    }
}