Jira REST API

There is a java solution that interacts with jira via the REST api to add groups, add filters, add dashboards and update schemes, fields to remove the word “migrated” from the names and descriptions of cloned items.
The solution is located in GitHub here: 
You will need to create a token on the destination instance
In the config.properties enter the instance url, the username and the token you have generated into the placeholders.
There is a classrunner class that runs the each class in in turn in the correct order. Run this. 
The following classes each perform a task
JiraGroupCreator – Creates the groups needed for the filters.
JiraFilterCreator – Creates the filters needed for the dashboards etc…
JiraDashboardCreator – Creates the dashboards for requirements, test cases and defects
JiraUpdateScreenScheme – updates the screen schemes name and description to remove the word migrated
JiraScreenUpdater – updates the screen names and descriptions to remove the word migrated
JiraFieldConfigurationRenamer – Updates the field configuration names and descriptions to remove the word migrated
JiraCustomFieldConfigurator – Updates custom fields that should be mandatory to mandatory 
CustomFieldUpdater - Updates custom field name and descriptions to remove the word mandatory from the name and description fields
IssueTypeScreenSchemeUpdater – Updates issue type screen scheme to remove the word migrated from the name and description
IssueTypeSchemeUpdater – Updates the issue type scheme to remove the word migrated from the name and description
