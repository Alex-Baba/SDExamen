import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File file = new File("/home/student/opt/1307A/Popescu Ion/student.xml");
        file.delete();

        req.setAttribute("deleted","Studentul a fost sters.");
        req.getRequestDispatcher("./index.jsp").forward(req, resp);
    }
}
