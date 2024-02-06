public class ClassRunner {

    public static void main(String[] args) {
        // List of class names to run in order
        String[] classNames = {
                "JiraGroupCreator",
                "JiraFilterCreator",
                "JiraDashboardCreator",
                "JiraUpdateScreenScheme",
                "JiraScreenUpdater",
                "JiraFieldConfigurationRenamer",
                "JiraCustomFieldConfiguration",
                "IssueTypeScreenSchemeUpdater",
                "IssueTypeSchemeUpdater",
                "CustomFieldUpdater"};

        for (String className : classNames) {
            try {
                // Load the class dynamically
                Class<?> clazz = Class.forName(className);

                // Check if the class has a main method
                try {
                    // Get the main method
                    java.lang.reflect.Method method = clazz.getMethod("main", String[].class);

                    // Invoke the main method with null arguments (assuming no command-line arguments needed)
                    method.invoke(null, (Object) null);
                } catch (NoSuchMethodException e) {
                    // If the method does not exist, handle the exception or print a message
                    System.out.println("Class " + className + " does not have a main method.");
                }

            } catch (ClassNotFoundException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
