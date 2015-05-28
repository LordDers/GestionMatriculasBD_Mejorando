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
import com.zubiri.matriculas.Matricula;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Modificar_Matricula
 */
public class Modificar_Matricula extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Modificar_Matricula() {
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
		Statement sentenciaUpdate = null;
		
		System.out.println("Empieza modificando");

		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		Profesor profesor = new Profesor("","","","","");
		Matricula matricula = new Matricula(request.getParameter("asignatura"), 0, profesor, 0, 0);
		Alumno alumno = new Alumno(request.getParameter("dniMatricula"), "", "", 0, "");
		
		System.out.println(alumno.getDni());
		System.out.println(matricula.getNombre());
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			sentenciaUpdate = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia alumno: " + alumno.getDni());
			System.out.println("Referencia asignatura: " + matricula.getNombre());
			
			sql = "SELECT matriculas.dni_alumno, matriculas.id_asignatura, personas.nombre AS 'alumno', personas.apellido, asignaturas.nombre AS 'asignatura' "+
					"FROM matriculas INNER JOIN alumnos ON matriculas.dni_alumno = alumnos.dni "+
					"INNER JOIN personas ON alumnos.dni = personas.dni "+
					"INNER JOIN asignaturas ON matriculas.id_asignatura = asignaturas.id_asignatura "+
					"WHERE matriculas.dni_alumno = \""+alumno.getDni()+"\" AND asignaturas.nombre = \""+matricula.getNombre()+"\"";
			
			System.out.println(sql);
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			while (buscar.next()) {
				cont++;
			}
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion != true) {
					formulario_modificar(response, alumno, matricula);
				} else {
					alumno.setDni(request.getParameter("dniMatricula"));
					matricula.setNombre(request.getParameter("asignatura"));
					matricula.setAnyoMatriculacion(Integer.parseInt(request.getParameter("anyo_matricula")));
					matricula.setPrecio(Integer.parseInt(request.getParameter("precio")));
					int id_asignatura = Integer.parseInt(request.getParameter("id"));
					
					System.out.println("DNI: " + alumno.getDni());
					System.out.println("Asignatura: " + matricula.getNombre());
					System.out.println("ID Asignatura: " + id_asignatura);
					System.out.println("Año de la matrícula: " + matricula.getAnyoMatriculacion());
					System.out.println("Precio: " + matricula.getPrecio());
					
					String cambios="";
					cambios="anyo_matriculacion = "+matricula.getAnyoMatriculacion()+",";
					cambios+=" precio = "+matricula.getPrecio()+"";
					
					try {
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);			        
						sentencia = con.createStatement();

						System.out.println("UPDATE matriculas SET "+cambios+" WHERE dni_alumno=\""+alumno.getDni()+"\" AND id_asignatura="+id_asignatura+"");
						
						String sqlUpdate;
						sqlUpdate="UPDATE matriculas SET "+cambios+" WHERE dni_alumno=\""+alumno.getDni()+"\" AND id_asignatura="+id_asignatura+"";

						int update = sentenciaUpdate.executeUpdate(sqlUpdate);
						
						System.out.println("Valor actualizar: " + update);
						if (update == 1) {
							response(response, "Se ha modificado la matricula del alumno " + alumno.getNombre() + " " + alumno.getApellido() + " con DNI " + alumno.getDni() + " en la asignatura " + matricula.getNombre() + "<br>" + cambios);
						} else {
							response(response, "¡Error! No se ha modificado la matricula del alumno " + alumno.getNombre() + " " + alumno.getApellido() + " con DNI " + alumno.getDni() + " en la asignatura " + matricula.getNombre());
						}
						con.close();
					} catch(ArrayIndexOutOfBoundsException e) {
						response(response, "No se encontró la matricula");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "El alumno " + alumno.getDni() + " no está matriculado en " + matricula.getNombre());
			}
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "No se encontro la asignatura");
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
		out.println("<a href='matriculas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void formulario_modificar(HttpServletResponse response, Alumno alumno, Matricula matricula) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		System.out.println("Se está modificando la matricula del alumno " + alumno.getDni() + " " + " en la asignatura " + matricula.getNombre());

		Connection con = null;	
		Statement sentencia = null;
		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia alumno: " + alumno.getDni());
			System.out.println("Referencia asignatura: " + matricula.getNombre());
			sql="SELECT matriculas.dni_alumno, matriculas.id_asignatura, matriculas.anyo_matriculacion, matriculas.precio, personas.nombre AS 'alumno', personas.apellido, asignaturas.nombre AS 'asignatura' "+
					"FROM matriculas INNER JOIN alumnos ON matriculas.dni_alumno = alumnos.dni "+
					"INNER JOIN personas ON alumnos.dni = personas.dni "+
					"INNER JOIN asignaturas ON matriculas.id_asignatura = asignaturas.id_asignatura "+
					"WHERE matriculas.dni_alumno = \""+alumno.getDni()+"\" AND asignaturas.nombre = \""+matricula.getNombre()+"\"";
			ResultSet buscar = sentencia.executeQuery(sql);

			Profesor profesor = new Profesor("","","","","");
			matricula = new Matricula(matricula.getNombre(), 0, profesor, 0, 0);
			alumno = new Alumno(alumno.getDni(), "", "", 0, "");
			
			int id_asignatura = -1;		
			while (buscar.next()) {
				alumno.setDni(buscar.getString("dni_alumno"));
				id_asignatura = buscar.getInt("id_asignatura");
				matricula.setNombre(buscar.getString("asignatura"));
				matricula.setAnyoMatriculacion(buscar.getInt("anyo_matriculacion"));
				matricula.setPrecio(buscar.getInt("precio"));
				alumno.setNombre(buscar.getString("alumno"));
				alumno.setApellido(buscar.getString("apellido"));
				
				System.out.println("DNI: " + alumno.getDni());
				System.out.println("Nombre: " + alumno.getNombre() + " " + alumno.getApellido());
				System.out.println("Asignatura matricula: " + matricula.getNombre());
				System.out.println("ID asignatura: " + id_asignatura);
				System.out.println("Año de matriculación: " + matricula.getAnyoMatriculacion());
				System.out.println("Precio: " + matricula.getPrecio());
			}

			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Modificar matricula </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<fieldset>	<legend> Modificar matricula " + alumno.getDni() + " " + matricula.getNombre() + "</legend>");
				out.println("<form name='modificar_matricula' method='post' onsubmit='return validacion_modificar_alumno()' action='Modificar_Matricula'>");
					out.println("<input name='gestion' hidden='true' type='text' value='modificar_matricula'/>");
					out.println("<input name='dniMatricula' type='text' value='" + alumno.getDni() + "' hidden='true'/> <br>");
					out.println("<label>DNI ALumno: </label> <input type='text' value='" + alumno.getDni() + "' disabled/>");
					out.println("<input name='asignatura' type='text' value='" + matricula.getNombre() + "' hidden='true'/> <br>");
					out.println("<label>Asignatura: </label> <input type='text' value='" + matricula.getNombre() + "' disabled/>");
					out.println("<input name='id' type='text' value='" + id_asignatura + "' hidden='true'/> <br>");
					out.println("<label> Año de matriculación: </label> <input name='anyo_matricula' type='text' value='" + matricula.getAnyoMatriculacion() + "'/> <br>");
					out.println("<label>Precio: </label> <input name='precio' type='text' id='precio' value='" + matricula.getPrecio() + "' /> <br>");
					out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\" value='true'></input>");
					out.println("<input type='submit' id='submit' value='Modificar'>");
				out.println("</form>");
			out.println("</fieldset>");
			out.println("<br> <a href='matriculas.html'> <button> Volver </button> </a>");
			out.println("</body>");
			out.println("</html>");

			con.close();

		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el vehiculo");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
