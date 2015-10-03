package es.eucm.rage.gameserver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * This handles requests to generate new "tracked" versions of the main game
 */
public class Server extends HttpServlet {
	 private static final Logger log = Logger.getLogger("GameServer");
	 private static String expectedKey;
	 private static String gameSource;
	 private static String gameTargetBase;
	 private static int nextGame = 1;
	 private static final String TRACKER_FILE = "track.txt";
	 private static final String TRACKER_DIR = "assets";
	 private static byte[] indexForm;

	 @Override public void init (ServletConfig config) throws ServletException {
		  super.init(config);
		  Enumeration<String> initParams = config.getInitParameterNames();
		  while (initParams.hasMoreElements()) {
				String p = initParams.nextElement();
				log.info("Init: '" + p + "' -> '" + config.getInitParameter(p) + "'");
		  }
		  expectedKey = config.getInitParameter("authKey");
		  gameTargetBase = config.getServletContext().getRealPath("/");
		  // undo webapp -> target -> gameserver
		  File commonBase = new File(gameTargetBase).getParentFile().getParentFile().getParentFile();
		  gameSource = new File(commonBase, config.getInitParameter("gameSource")).getPath();
		  log.info("gameSource: " + new File(gameSource).getAbsolutePath());
		  log.info("gameTargetBase: " + new File(gameTargetBase).getAbsolutePath());
		  File nextCandidate = new File(gameTargetBase, "" + nextGame);
		  while (nextCandidate.exists()) {
		  		nextGame ++;
				nextCandidate = new File(gameTargetBase, "" + nextGame);
		  }
		  log.info("nextGame: " + nextGame);
		  try {
				indexForm = Files.readAllBytes(new File(gameTargetBase, "/WEB-INF/index.html").toPath());
		  } catch (IOException e) {
				throw new ServletException("Could not read indexForm", e);
		  }

	 }

	 public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  response.setContentType("text/html");
		  String authKey = request.getParameter("authKey");
		  if ( ! expectedKey.equals(authKey)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().println("Bad auth key.");
		  };

		  String trackCode = request.getParameter("trackCode");
		  String serverUrl = request.getParameter("serverUrl");
		  try {
				String result = createTrackedGame("" + nextGame, serverUrl + ";" + trackCode);
				nextGame ++;
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().println("Ok - game available at " +
					request.getRequestURL().toString().replace(request.getRequestURI(), "/" + result));
		  } catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Error - " + e);
				log.log(Level.WARNING, "Error processing request '" + trackCode + "' '" + serverUrl + "': " + e, e);
		  }
	 }

	 public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  response.setContentType("text/html");
		  response.setStatus(HttpServletResponse.SC_OK);
		  response.getOutputStream().write(indexForm);
	 }

	 public static void copy(Path source, Path target) throws IOException {
		  log.info("Copying " + source + " to " + target);
		  if (source.toFile().isDirectory()) {
				// 8 major Java versions and still no easy way to copy trees...
				FileUtils.copyDirectory(source.toFile(), target.toFile());
		  } else {
				Files.copy(source, target);
		  }
	 }

	 /**
	  * This will not work with Jetty when using JettyMavenPlugin, as the traditional
	  * addAliasCheck call is not supported by the maven WebAppContext:
	  * java.lang.IllegalStateException: No Method: <Call name="addAliasCheck">
	  *     <Arg><New class="org.eclipse.jetty.server.handler.AllowSymLinkAliasChecker"/></Arg></Call>
	  *      on class org.eclipse.jetty.maven.plugin.JettyWebAppContext
	  * @throws IOException
	  */
	 public static void symlink(Path source, Path target) throws IOException {
		  log.info("Linking " + source + " to " + target);
		  // counter-intuitive given unix syntax: ln -s source target...
		  Files.createSymbolicLink(target, source);
	 }

	 public static String createTrackedGame(String id, String text) throws IOException {
		  File targetDir = new File(gameTargetBase, id);
		  log.info("Creating new game-dir in " + targetDir.getAbsolutePath());

		  if (targetDir.exists() || ! targetDir.mkdir()) {
				throw new IllegalArgumentException("Could not create target dir " + targetDir.getAbsolutePath());
		  }
		  for (File f : new File(gameSource).listFiles()) {
				if ( ! f.getName().equals(TRACKER_DIR)) {
					 Path s = f.toPath().toAbsolutePath();
					 Path t = new File(targetDir, f.getName()).toPath().toAbsolutePath();
					 copy(s, t);
				} else {
					 File trackerDir = new File(targetDir, TRACKER_DIR);
					 trackerDir.mkdir();
					 for (File tf : f.listFiles()) {
						  Path t = new File(trackerDir, tf.getName()).toPath().toAbsolutePath();
						  if (tf.getName().equals(TRACKER_FILE)) {
								Files.write(t, text.getBytes());
						  } else {
								Path s = tf.toPath().toAbsolutePath();
								copy(s, t);
						  }
					 }
				}
		  }

		  return new File(gameTargetBase).toURI().relativize(targetDir.toURI()).getPath();
	 }
}
