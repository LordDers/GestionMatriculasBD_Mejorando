

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
import com.zubiri.matriculas.Persona;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Modificar_Persona
 */
public class Modificar_Persona extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Modificar_Persona() {
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

		String dni = request.getParameter("dniPersona");
		Persona persona = new Profesor(request.getParameter("dniPersona"), "", "", "", "");
		Profesor profesor = new Profesor(request.getParameter("dniPersona"), "", "", "", "");
		Alumno alumno = new Alumno(request.getParameter("dniPersona"), "", "", 0, "");

		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + dni);
			
			sql="SELECT * FROM persona WHERE dni=\""+dni+"\"";
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			while (buscar.next()) {
				cont++;
			}
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion != true) {
					formulario_modificar(response,request.getParameter("dniPersona"));
				} else {
					Statement sentenciaAlumno = null;
					Statement sentenciaProfesor = null;
					String sqlUpdate;
					try {					
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
						
						// Actualizar alumno
						sentenciaAlumno = con.createStatement();

						String sqlSelectAlumno="SELECT persona.dni, persona.nombre, persona.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM persona INNER JOIN alumnos ON persona.dni = alumnos.dni WHERE persona.dni=\""+dni+"\"";
						ResultSet mostrarAlumno = sentenciaAlumno.executeQuery(sqlSelectAlumno);
						
						String cambiosPersona="";
						String cambiosAlumno="";
						String cambiosProfesor="";
						
						String nombre = null;
						String apellido = null;
						Integer anyo = 0;
						String ciclo = null;
						while (mostrarAlumno.next()) {
							// Tabla MySQL
							dni = mostrarAlumno.getString("dni");
							nombre = mostrarAlumno.getString("nombre");
							apellido = mostrarAlumno.getString("apellido");
							anyo = mostrarAlumno.getInt("anyo_inscripcion");
							ciclo = mostrarAlumno.getString("ciclo");
							
							// Formulario
							alumno.setDni(request.getParameter("dniPersona"));
							alumno.setNombre(request.getParameter("nombre"));
							alumno.setApellido(request.getParameter("apellido"));
							alumno.setAnyoInscripcion(Integer.parseInt(request.getParameter("anyo_inscripcion")));
							alumno.setCiclo(request.getParameter("ciclo"));

							cambiosAlumno="dni = \""+alumno.getDni()+"\",";
							cambiosAlumno+=" anyo_inscripcion = "+alumno.getAnyoInscripcion()+",";
							cambiosAlumno+=" ciclo = \""+alumno.getCiclo()+"\"";
							
							cambiosPersona="dni = \""+alumno.getDni()+"\",";
							cambiosPersona+=" nombre = \""+alumno.getNombre()+"\",";
							cambiosPersona+=" apellido = \""+alumno.getApellido()+"\"";
							
							Alumno alumnoEncontrado = new Alumno(dni,nombre,apellido,anyo,ciclo);
							
							System.out.println("Dni: " + alumnoEncontrado.getDni());
							System.out.println("Nombre: " + alumnoEncontrado.getNombre());
							System.out.println("Apellido: " + alumnoEncontrado.getApellido());
							System.out.println("Ciclo: " + alumnoEncontrado.getCiclo());
							
							System.out.println("UPDATE persona SET "+cambiosPersona+" WHERE dni=\""+persona.getDni()+"\"");
							System.out.println("UPDATE alumnos SET "+cambiosAlumno+" WHERE dni=\""+alumno.getDni()+"\"");
							
							sqlUpdate="UPDATE persona SET "+cambiosPersona+" WHERE dni=\""+persona.getDni()+"\"";
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
						}
						
						// Actualizar profesor
						sentenciaProfesor = con.createStatement();

						String sqlSelectProfesor="SELECT persona.dni, persona.nombre, persona.apellido, profesores.titulacion, profesores.departamento FROM persona INNER JOIN profesores ON persona.dni = profesores.dni WHERE persona.dni=\""+dni+"\"";
						ResultSet mostrarProfesor = sentenciaProfesor.executeQuery(sqlSelectProfesor);
						
						String titulacion = null;
						String departamento = null;
						while (mostrarProfesor.next()) {
							// Tabla MySQL
							dni = mostrarProfesor.getString("dni");
							nombre = mostrarProfesor.getString("nombre");
							apellido = mostrarProfesor.getString("apellido");
							titulacion = mostrarProfesor.getString("titulacion");
							departamento = mostrarProfesor.getString("departamento");
							
							// Formulario
							profesor.setDni(request.getParameter("dniPersona"));
							profesor.setNombre(request.getParameter("nombre"));
							profesor.setApellido(request.getParameter("apellido"));
							profesor.setTitulacion(request.getParameter("titulacion"));
							profesor.setDepartamento(request.getParameter("departamento"));

							cambiosProfesor="dni = \""+profesor.getDni()+"\",";
							cambiosProfesor+=" titulacion = \""+profesor.getTitulacion()+"\",";
							cambiosProfesor+=" departamento = \""+profesor.getDepartamento()+"\"";
							
							cambiosPersona="dni = \""+profesor.getDni()+"\",";
							cambiosPersona+=" nombre = \""+profesor.getNombre()+"\",";
							cambiosPersona+=" apellido = \""+profesor.getApellido()+"\"";
							
							Profesor profesorEncontrado = new Profesor(dni,nombre,apellido, titulacion, departamento);
							
							System.out.println("Dni: " + profesorEncontrado.getDni());
							System.out.println("Nombre: " + profesorEncontrado.getNombre());
							System.out.println("Apellido: " + profesorEncontrado.getApellido());
							System.out.println("Departamento: " + profesorEncontrado.getDepartamento());
							
							System.out.println("UPDATE persona SET "+cambiosPersona+" WHERE dni=\""+persona.getDni()+"\"");
							System.out.println("UPDATE profesores SET "+cambiosProfesor+" WHERE dni=\""+profesor.getDni()+"\"");
							
							sqlUpdate="UPDATE persona SET "+cambiosPersona+" WHERE dni=\""+persona.getDni()+"\"";
							String sqlProfesor="UPDATE profesores SET "+cambiosProfesor+" WHERE dni=\""+profesor.getDni()+"\"";
							
							int updateProfesor = sentencia.executeUpdate(sqlProfesor);
							int updatePersona = sentencia.executeUpdate(sqlUpdate);
							
							System.out.println("Valor actualizar: " + updatePersona);
							if (updatePersona == 1) {
								if (updateProfesor == 1) {
									response(response, "Se ha modificado el profesor con DNI " + profesor.getDni() + ".<br>" + cambiosPersona);
								}
							} else {
								response(response, "¡Error! No se ha modificado el profesor, compruebe el DNI: " + profesor.getDni());
							}
						} // while
					} catch(ArrayIndexOutOfBoundsException e) {
						//response(response, "No se encontró el profesor");
					} catch(Exception e) {
						e.printStackTrace();
					}
				} // else cont	
			} else { // if cont
				response(response, "No se encontró la persona");
			}
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "No se encontro la persona");
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
		out.println("<a href='index.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void formulario_modificar(HttpServletResponse response, String referencia) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		
		PrintWriter out = response.getWriter();
		
		System.out.println("Se está modificando la persona con DNI: " + referencia);

		Connection con = null;	
		Statement sentenciaAlumno = null;
		Statement sentenciaProfesor = null;
		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentenciaAlumno = con.createStatement();
			sentenciaProfesor = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: "+referencia);
			/*sql="SELECT * FROM alumnos WHERE dni=\""+referencia+"\"";
			ResultSet buscar = sentencia.executeQuery(sql);*/
			sql="SELECT persona.dni, persona.nombre, persona.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM persona INNER JOIN alumnos ON persona.dni = alumnos.dni WHERE persona.dni=\""+referencia+"\"";
			ResultSet buscarAlumno = sentenciaAlumno.executeQuery(sql);
			
			sql="SELECT persona.dni, persona.nombre, persona.apellido, profesores.titulacion, profesores.departamento FROM persona INNER JOIN profesores ON persona.dni = profesores.dni WHERE persona.dni=\""+referencia+"\"";
			ResultSet buscarProfesor = sentenciaProfesor.executeQuery(sql);
			
			String dni = null;
			String nombre = null;
			String apellido = null;
			Integer anyo_inscripcion = 0;
			String ciclo = null;
			String titulacion = null;
			String departamento = null;
			
			Alumno alumnoEncontrado = null;
			
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Modificar alumno </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			while (buscarAlumno.next()) {
				dni = buscarAlumno.getString("dni");
				nombre = buscarAlumno.getString("nombre");
				apellido = buscarAlumno.getString("apellido");
				anyo_inscripcion = buscarAlumno.getInt("anyo_inscripcion");
				ciclo = buscarAlumno.getString("ciclo");
				
				alumnoEncontrado = new Alumno(dni,nombre,apellido,anyo_inscripcion,ciclo);
				
				System.out.println("Dni: " + alumnoEncontrado.getDni());
				System.out.println("Nombre: " + alumnoEncontrado.getNombre());
				System.out.println("Apellido: " + alumnoEncontrado.getApellido());
				System.out.println("Ciclo: " + alumnoEncontrado.getCiclo());
			
				out.println("<fieldset>	<legend> Modificar alumno " + alumnoEncontrado.getDni() + "</legend>");
					out.println("<form name='modificar_persona' method='post' onsubmit='return validacion_modificar_alumno()' action='Modificar_Persona'>");
						out.println("<input name='gestion' hidden='true' type='text' value='modificar_persona'/>");
						out.println("<input name='dniPersona' type='text' value='" + alumnoEncontrado.getDni() + "' hidden='true'/> <br>");
						out.println("<label>DNI a modificar: </label> <input type='text' value='" + alumnoEncontrado.getDni() + "' disabled/> <br>");
						out.println("<label>Nombre: </label> <input name='nombre' type='text' id='nombre' value='" + alumnoEncontrado.getNombre() + "' /> <br>");
						out.println("<label>Apellido: </label> <input name='apellido' type='text' id='apellido' value='"+alumnoEncontrado.getApellido()+"' /> <br>");
						out.println("<label>Año de inscripción </label> <input name='anyo_inscripcion' type='text' id='anyo_inscripcion' value='"+alumnoEncontrado.getAnyoInscripcion()+"' /> <br>");
						out.println("<label> Ciclo </label> <input name='ciclo' type='text' id='ciclo' value='"+alumnoEncontrado.getCiclo()+"' /> <br>");
						out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\" value='true'></input>");
						out.println("<input type='submit' id='submit' value='Modificar'>");
					out.println("</form>");
				out.println("</fieldset>");
				out.println("<br> <a href='index.html'> <button> Volver </button> </a>");
			}
			
			while (buscarProfesor.next()) {
				dni = buscarProfesor.getString("dni");
				nombre = buscarProfesor.getString("nombre");
				apellido = buscarProfesor.getString("apellido");
				titulacion = buscarProfesor.getString("titulacion");
				departamento = buscarProfesor.getString("departamento");
				
				Profesor profesorEncontrado = new Profesor(dni,nombre,apellido, titulacion, departamento);
				
				System.out.println("Dni: " + profesorEncontrado.getDni());
				System.out.println("Nombre: " + profesorEncontrado.getNombre());
				System.out.println("Apellido: " + profesorEncontrado.getApellido());
				System.out.println("Departamento: " + profesorEncontrado.getDepartamento());
			
				out.println("<fieldset>	<legend> Modificar profesor " + profesorEncontrado.getDni() + "</legend>");
					out.println("<form name='modificar_persona' method='post' onsubmit='return validacion_modificar_alumno()' action='Modificar_Persona'>");
						out.println("<input name='gestion' hidden='true' type='text' value='modificar_persona'/>");
						out.println("<input name='dniPersona' type='text' value='" + profesorEncontrado.getDni() + "' hidden='true'/> <br>");
						out.println("<label>DNI a modificar: </label> <input type='text' value='" + profesorEncontrado.getDni() + "' disabled/> <br>");
						out.println("<label>Nombre: </label> <input name='nombre' type='text' id='nombre' value='" + profesorEncontrado.getNombre() + "' /> <br>");
						out.println("<label>Apellido: </label> <input name='apellido' type='text' id='apellido' value='"+profesorEncontrado.getApellido()+"' /> <br>");
						out.println("<label>Titulación </label> <input name=\"titulacion\" type=\"text\" value='"+profesorEncontrado.getTitulacion()+"'/>  <br>");
						out.println("<label> Departamento </label> <input name=\"departamento\" type=\"text\" value='"+profesorEncontrado.getDepartamento()+"'/>  <br>");
						out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\" value='true'></input>");
						out.println("<input type='submit' id='submit' value='Modificar'>");
					out.println("</form>");
				out.println("</fieldset>");
				out.println("<br> <a href='index.html'> <button> Volver </button> </a>");
			}
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
