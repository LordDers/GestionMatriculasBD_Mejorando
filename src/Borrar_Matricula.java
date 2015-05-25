import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zubiri.matriculas.Alumno;
import com.zubiri.matriculas.Asignatura;
import com.zubiri.matriculas.Matricula;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Borrar_Matricula
 */
public class Borrar_Matricula extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Borrar_Matricula() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType( "text/html; charset=iso-8859-1" );
		
		Connection con = null;	
		Statement sentencia = null;
		System.out.println("Borrando");
		
		Profesor profesor = new Profesor("","","","","");
		Matricula matricula = new Matricula(request.getParameter("asignatura"), 0, profesor, 0, 0);
		Asignatura asignatura = new Asignatura(request.getParameter("asignatura"), 0, profesor);
		Alumno alumno = new Alumno(request.getParameter("dniMatricula"), "", "", 0, "");

		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia alumno: " + alumno.getDni());
			sql = "SELECT matriculas.dni_alumno, matriculas.id_asignatura, persona.nombre AS 'alumno', persona.apellido, asignaturas.nombre AS 'asignatura' "+
					"FROM matriculas INNER JOIN alumnos ON matriculas.dni_alumno = alumnos.dni "+
					"INNER JOIN persona ON alumnos.dni = persona.dni "+
					"INNER JOIN asignaturas ON matriculas.id_asignatura = asignaturas.id_asignatura "+
					"WHERE matriculas.dni_alumno = \""+alumno.getDni()+"\" AND asignaturas.nombre = \""+asignatura.getNombre()+"\"";
			
			System.out.println(sql);
			
			ResultSet buscar = sentencia.executeQuery(sql);
			
			int cont = 0;
			int id_asignatura = -1;
			
			while (buscar.next()) {
				alumno.setDni(buscar.getString("dni_alumno"));
				id_asignatura = buscar.getInt("id_asignatura");
				matricula.setNombre(buscar.getString("asignatura"));
				alumno.setNombre(buscar.getString("alumno"));
				alumno.setApellido(buscar.getString("apellido"));
				
				System.out.println("DNI: " + alumno.getDni());
				System.out.println("Nombre: " + alumno.getNombre() + " " + alumno.getApellido());
				System.out.println("Asignatura matricula: " + matricula.getNombre());
				System.out.println("ID asignatura: " + id_asignatura);
				
				cont++;
			}
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion!=true) {
					confirmacion=false;
					response(response, "¿Seguro que quieres desmatricular al alumno " + alumno.getDni() + "?", matricula, alumno);
				} else {
					try {
						String sqlDelete;
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);			        
						sentencia = con.createStatement();

						System.out.println("DELETE FROM matriculas WHERE dni_alumno=\""+alumno.getDni()+"\" AND id_asignatura="+id_asignatura+"");
						
						sqlDelete="DELETE FROM matriculas WHERE dni_alumno=\""+alumno.getDni()+"\" AND id_asignatura="+id_asignatura+"";

						int borrar = sentencia.executeUpdate(sqlDelete);
						
						System.out.println("Valor borrar: " + borrar);
						if (borrar == 1) {
							response(response, "Se ha desmatriculado al alumno " + alumno.getNombre() + " " + alumno.getApellido() + " con DNI " + alumno.getDni() + " de la asignatura " + matricula.getNombre());
						} else {
							response(response, "No Se ha desmatriculado al alumno " + alumno.getNombre() + " " + alumno.getApellido() + " con DNI " + alumno.getDni() + " de la asignatura " + matricula.getNombre());
						}
						con.close();			    	
					} catch(ArrayIndexOutOfBoundsException e) {
						//response(response, "no se encontro la asignatura");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "El alumno " + alumno.getDni() + " no está matriculado en " + matricula.getNombre());
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro la asignatura");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void response(HttpServletResponse response, String msg) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Respuesta </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");				
		out.println("<p>" + msg + "</p>");
		out.println("<a href='matriculas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void response(HttpServletResponse response,String msg, Matricula matricula, Alumno alumno) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Borrar matricula </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body align='center'>");
		out.println("<p>" + msg + "</p>");
		out.println("<p>Asignatura: " + matricula.getNombre() + "</p>");
		out.println("<form name=\"borrar_matricula\" method=\"post\" action=\"Borrar_Matricula\" style='margin-right: auto;'>");
			out.println("<input name='gestion' hidden='true' type='text'  value='borrar_matricula'/>");
			out.println("<input name=\"asignatura\" hidden=\"true\" type=\"text\"  value=" + matricula.getNombre() + "></input>");
			out.println("<input name=\"dniMatricula\" hidden=\"true\" type=\"text\"  value=" + alumno.getDni() + "></input>");
			out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\"  value='true'></input>");
			out.println("<p> <input type='submit' id='submit' value='Borrar'> </p>");
		out.println("</form>");
		out.println("<a href='matriculas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}

}
