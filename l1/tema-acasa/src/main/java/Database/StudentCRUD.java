package Database;

import beans.StudentBean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentCRUD {

    public void addStudent(StudentBean student) throws SQLException {
        PreparedStatement stm = SQLiteConn.getInstance().getConn().prepareStatement(
                "INSERT INTO student(nume,prenume,varsta,grupa,medie,credite) VALUES (?,?,?,?,?,?)"
        );

        stm.setString(1,student.getNume());
        stm.setString(2,student.getPrenume());
        stm.setInt(3,student.getVarsta());
        stm.setString(4,student.getGrupa());
        stm.setDouble(5,student.getMedie());
        stm.setInt(6,student.getCredite());

        stm.executeUpdate();
    }

    public StudentBean getStudentById(Integer id) throws SQLException {

        PreparedStatement stm = SQLiteConn.getInstance().getConn().prepareStatement(
                "Select * from student where id=?"
        );
        stm.setInt(1, id);

        ResultSet rs = stm.executeQuery();
        StudentBean student = new StudentBean();
        while(rs.next()){
            student.setNume(rs.getString("nume"));
            student.setPrenume(rs.getString("prenume"));
            student.setVarsta(rs.getInt("varsta"));
            student.setGrupa(rs.getString("grupa"));
            student.setCredite(rs.getInt("credite"));
            student.setMedie(rs.getDouble("medie"));
            student.setId(id);
        }

        return student;
    }

    public List<StudentBean> getAllStudents() throws SQLException {
        List<StudentBean> list = new ArrayList<>();

        PreparedStatement stm = SQLiteConn.getInstance().getConn().prepareStatement(
                "Select * from student"
        );

        ResultSet rs = stm.executeQuery();
        while(rs.next()){
            StudentBean student = new StudentBean();
            student.setNume(rs.getString("nume"));
            student.setPrenume(rs.getString("prenume"));
            student.setVarsta(rs.getInt("varsta"));
            student.setGrupa(rs.getString("grupa"));
            student.setCredite(rs.getInt("credite"));
            student.setMedie(rs.getDouble("medie"));
            student.setId(rs.getInt("id"));

            list.add(student);
        }

        return list;
    }

    public void updateStudent(Integer id, StudentBean newStudent) throws SQLException {
        PreparedStatement stm = SQLiteConn.getInstance().getConn().prepareStatement(
                "Update student set nume=?, prenume=?, varsta=?, grupa=?, credite=?, medie=? where id=?"
        );

        stm.setString(1, newStudent.getNume());
        stm.setString(2, newStudent.getNume());
        stm.setInt(3, newStudent.getVarsta());
        stm.setString(4, newStudent.getGrupa());
        stm.setInt(5, newStudent.getCredite());
        stm.setDouble(6, newStudent.getMedie());
        stm.setInt(7, id);

        stm.executeUpdate();
    }

    public void deleteStudent(Integer id) throws SQLException {
        PreparedStatement stm = SQLiteConn.getInstance().getConn().prepareStatement(
                "Delete from student where id=?"
        );

        stm.setInt(1, id);

        stm.executeUpdate();
    }

    public StudentBean getStudentByFullName(String nume, String prenume) throws SQLException {
        PreparedStatement stm = SQLiteConn.getInstance().getConn().prepareStatement(
                "Select * from student where nume=? and prenume=?"
        );
        stm.setString(1,nume);
        stm.setString(2,prenume);

        ResultSet rs = stm.executeQuery();
        StudentBean student = new StudentBean();

        while(rs.next()){
            student.setNume(rs.getString("nume"));
            student.setPrenume(rs.getString("prenume"));
            student.setVarsta(rs.getInt("varsta"));
            student.setGrupa(rs.getString("grupa"));
            student.setCredite(rs.getInt("credite"));
            student.setMedie(rs.getDouble("medie"));
            student.setId(rs.getInt("id"));
        }

        return student;
    }
}
