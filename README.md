# springboot-thymeleaf
Spring Boot, Spring MVC, Thymeleaf, Material Design, Javascript.

This system  registers users on a form with validations and offers the option to upload a document (curriculum).  
Once saved, the user will be added to a list with other users, who have already been saved in the system. 
In this list, when you click on the user's name, you will be redirected to the phone number page. It is also possible 
to download the CV from the list of saved users. In this system there is only one standard user to access the system 
and this does not appear in the list of saved users, they are different entities. And users created through the form 
do not have a login to access the system. 
Unfortunately, due to the limited capacity of the AWS instance to deploy war files, it was not possible to deploy the
project as planned and a feature was removed. The Jasper Report library has been removed and it is not possible to 
generate reports in this system for now, until a new solution is found.
