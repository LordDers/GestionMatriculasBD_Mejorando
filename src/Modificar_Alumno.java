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

/**
 * Servlet implementation class Modificar_Alumno
 */
public class Modificar_Alumno extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Modificar_Alumno() {
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
		
		System.out.println("Empieza modificando");

		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		Alumno alumno = new Alumno(request.getParameter("dniAlumno"), "", "", 0, "");
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + alumno.getDni());
			
			sql="SELECT persona.dni, persona.nombre, persona.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM persona INNER JOIN alumnos ON persona.dni = alumnos.dni WHERE persona.dni=\""+alumno.getDni()+"\"";
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			while (buscar.next()) {
				cont++;
			}
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion != true) {
					formulario_modificar(response,request.getParameter("dniAlumno"));
				} else {
					alumno.setDni(request.getParameter("dniAlumno"));
					alumno.setNombre(request.getParameter("nombre"));
					alumno.setApellido(request.getParameter("apellido"));
					alumno.setAnyoInscripcion(Integer.parseInt(request.getParameter("anyo_inscripcion")));
					alumno.setCiclo(request.getParameter("ciclo"));
					
					String cambiosPersona="";					
					String cambiosAlumno="";
					
					cambiosAlumno="dni = \""+alumno.getDni()+"\",";
					cambiosAlumno+=" anyo_inscripcion = "+alumno.getAnyoInscripcion()+",";
					cambiosAlumno+=" ciclo = \""+alumno.getCiclo()+"\"";
					
					cambiosPersona="dni = \""+alumno.getDni()+"\",";
					cambiosPersona+=" nombre = \""+alumno.getNombre()+"\",";
					cambiosPersona+=" apellido = \""+alumno.getApellido()+"\"";
					
					try {
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);			        
						sentencia = con.createStatement();
						
						System.out.println("UPDATE persona SET "+cambiosPersona+" WHERE dni=\""+alumno.getDni()+"\"");
						System.out.println("UPDATE alumnos SET "+cambiosAlumno+" WHERE dni=\""+alumno.getDni()+"\"");
						
						String sqlUpdate;
						sqlUpdate="UPDATE persona SET "+cambiosPersona+" WHERE dni=\""+alumno.getDni()+"\"";
						String sqlAlumno="UPDATE alumnos SET "+cambiosAlumno+" WHERE dni=\""+alumno.getDni()+"\"";
						
						int updateAlumno = sentencia.executeUpdate(sqlAlumno);
						int updatePersona = sentencia.executeUpdate(sqlUpdate);
						
						System.out.println("Valor actualizar: " + updatePersona);
						if (updatePersona == 1) {
							if (updateAlumno == 1) {
								response(response, "Se ha modificado el alumno con DNI " + alumno.getDni() + ".<br>" + cambiosPersona);
							}
						} else {
							response(response, "¡Error! No se ha modificado el alumno, compruebe el DNI: " + alumno.getDni());
						}
						con.close();
					} catch(ArrayIndexOutOfBoundsException e) {
						response(response, "No se encontró el alumno");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "No se encontró el alumno");
			}
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "No se encontro el alumno");
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
		out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void formulario_modificar(HttpServletResponse response, String referencia) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		System.out.println("Se está modificando el alumnos con DNI: " + referencia);

		Connection con = null;	
		Statement sentencia = null;
		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: "+referencia);
			sql="SELECT persona.dni, persona.nombre, persona.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM persona INNER JOIN alumnos ON persona.dni = alumnos.dni WHERE persona.dni=\""+referencia+"\"";
			ResultSet buscar = sentencia.executeQuery(sql);

			Alumno alumno = new Alumno("", "", "", 0, "");
					
			while (buscar.next()) {
				alumno.setDni(buscar.getString("dni"));
				alumno.setNombre(buscar.getString("nombre"));
				alumno.setApellido(buscar.getString("apellido"));
				alumno.setAnyoInscripcion(Integer.parseInt(buscar.getString("anyo_inscripcion")));
				alumno.setCiclo(buscar.getString("ciclo"));
				
				System.out.println("Dni: " + alumno.getDni());
				System.out.println("Nombre: " + alumno.getNombre());
				System.out.println("Apellido: " + alumno.getApellido());
				System.out.println("Año de inscripción: " + alumno.getAnyoInscripcion());
				System.out.println("Ciclo: " + alumno.getCiclo());
			}

			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Modificar profesor </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<fieldset>	<legend> Modificar Alumno " + referencia + "</legend>");
				out.println("<form name='modificar_alumno' method='post' onsubmit='return validacion_modificar_alumno()' action='Modificar_Alumno'>");
					out.println("<input name='gestion' hidden='true' type='text' value='modificar_vehiculo'/>");
					out.println("<input name='dniAlumno' type='text' value='" + referencia + "' hidden='true'/> <br>");
					out.println("<label>DNI a modificar: </label> <input type='text' value='" + referencia + "' disabled/> <br>");
					out.println("<label>Nombre: </label> <input name='nombre' type='text' id='nombre' value='" + alumno.getNombre() + "' /> <br>");
					out.println("<label>Apellido: </label> <input name='apellido' type='text' id='apellido' value='"+alumno.getApellido()+"' /> <br>");
					out.println("<label>Año de inscripción </label> <input name='anyo_inscripcion' type='text' id='anyo_inscripcion' value='"+alumno.getAnyoInscripcion()+"' /> <br>");
					out.println("<label> Ciclo </label> <input name='ciclo' type='text' id='ciclo' value='"+alumno.getCiclo()+"' /> <br>");
					out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\" value='true'></input>");
					out.println("<input type='submit' id='submit' value='Modificar'>");
				out.println("</form>");
			out.println("</fieldset>");
			out.println("<br> <a href='alumnos.html'> <button> Volver </button> </a>");
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
