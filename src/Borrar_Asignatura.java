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

import com.zubiri.matriculas.Asignatura;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Borrar_Asignatura
 */
public class Borrar_Asignatura extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Borrar_Asignatura() {
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
		Asignatura asignatura = new Asignatura(request.getParameter("asignatura"), 0, profesor);

		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + asignatura.getNombre());
			sql="SELECT profesores.dni, personas.nombre AS 'profesor', personas.apellido, profesores.titulacion, profesores.departamento, asignaturas.nombre AS 'asignatura', asignaturas.creditos "+
					"FROM (personas INNER JOIN profesores ON personas.dni = profesores.dni) "+
					"INNER JOIN asignaturas ON profesores.dni = asignaturas.dni_profesor "+
					"WHERE asignaturas.nombre=\""+asignatura.getNombre()+"\"";
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			while (buscar.next()) {
				cont++;
			}
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion!=true) {
					confirmacion=false;
					response(response, "¿Seguro que quieres borrar la asignatura?", asignatura.getNombre());
				} else {
					try {
						String sqlDelete;
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);			        
						sentencia = con.createStatement();

						System.out.println("DELETE FROM asignaturas where nombre=\""+asignatura.getNombre()+"\"");
						
						sqlDelete="DELETE FROM asignaturas where nombre=\""+asignatura.getNombre()+"\"";

						int borrarAsignatura = sentencia.executeUpdate(sqlDelete);
						
						System.out.println("Valor borrar: " + borrarAsignatura);
						if (borrarAsignatura == 1) {
							response(response, "Se ha borrado la asignatura " + asignatura.getNombre());
						} else {
							response(response, "No se ha borrado la asignatura, compruebe el nombre: " + asignatura.getNombre() + ".");
						}
						con.close();			    	
					} catch(ArrayIndexOutOfBoundsException e) {
						//response(response, "no se encontro la asignatura");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "No se encontró la asignatura");
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro la asignatura");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void response(HttpServletResponse response,String msg) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Respuesta </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");				
		out.println("<p>" + msg + "</p>");
		out.println("<a href='asignaturas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void response(HttpServletResponse response,String msg ,String asignatura) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Borrar asignatura </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body align='center'>");
		out.println("<p>" + msg + "</p>");
		out.println("<p>DNI: " + asignatura + "</p>");
		out.println("<form name=\"borrar_asignatura\" method=\"post\" action=\"Borrar_Asignatura\" style='margin-right: auto;'>");
			out.println("<input name='gestion' hidden='true' type='text'  value='borrar_asignatura'/>");
			out.println("<input name=\"asignatura\" hidden=\"true\" type=\"text\"  value=" + asignatura + "></input>");
			out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\"  value='true'></input>");
			out.println("<p> <input type='submit' id='submit' value='Borrar'> </p>");
		out.println("</form>");
		out.println("<a href='asignaturas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}

}
