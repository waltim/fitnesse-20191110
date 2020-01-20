package fitnesse.responders.editing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fitnesse.components.TraversalListener;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;
import util.GracefulNamer;

public class TemplateUtil {

  public static List<String> getTemplatesFromUncles(WikiPage page) {
    final List<String> templatePaths = new ArrayList<>();
    page.getPageCrawler().traverseUncles("TemplateLibrary", (WikiPage uncle) -> {
        uncle.getChildren().stream().map((template) -> new WikiPagePath(template)).map((templatePath) -> {
            templatePath.makeAbsolute();
            return templatePath;
        }).forEachOrdered((templatePath) -> {
            templatePaths.add(PathParser.render(templatePath));
        });
    });
    return templatePaths;
  }

  /**
   * @param page WikiPage
   * @return A set of pages that apply for template page snippets.
   */
  public static Map<String, String> getTemplateMap(WikiPage page){
    return getShortTemplateNames(getTemplatesFromUncles(page));
  }

  /**
   * @param page WikiPage
   * @return A map {name: path} of pages that apply for "New pages"
   */
  public static Map<String, String> getTemplatePageMap(WikiPage page){
    return getPageNames(getTemplatesFromUncles(page));
  }

  static Map<String, String> getPageNames(List<String> templatePaths) {
    Map<String, String> pathsAndNames = new TreeMap<>();
    templatePaths.forEach((path) -> {
        final String pageName = getPageName(path);
          if (!pathsAndNames.containsKey(pageName)) {
              pathsAndNames.put(pageName, path);
          }
      });
    return pathsAndNames;
  }

  static String getPageName(String path) {
    return GracefulNamer.regrace(path.substring(path.lastIndexOf('.') + 1));
  }

  static Map<String, String> getShortTemplateNames(List<String> templatePaths) {
    Map<String, String> pathsAndNames = new TreeMap<>();
    templatePaths.forEach((path) -> {
        pathsAndNames.put(getShortTemplateName(path), path);
      });
    return pathsAndNames;
  }

  static String getShortTemplateName(String path) {
    String pathCopy = path;

    String templateName = pathCopy.substring(pathCopy.lastIndexOf('.') + 1);
    pathCopy = pathCopy.substring(0, pathCopy.lastIndexOf('.'));

    String templateLibraryString = pathCopy.substring(pathCopy.lastIndexOf('.') + 1);
    pathCopy = pathCopy.substring(0, pathCopy.lastIndexOf('.'));
    if(templateLibraryString.equals("TemplateLibrary")) {
      templateLibraryString = "_";
    }

    String parentName = pathCopy.substring(pathCopy.lastIndexOf('.') + 1);

    return parentName + "." + templateLibraryString + "." + templateName;
  }
}
