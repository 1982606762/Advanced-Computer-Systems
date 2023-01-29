1. 

2. 

   1. In which sense is the architecture strongly modular?

      Each of its data structures is split into different classes, and also its methods like read and write are defined separately.

   2. What kind of isolation and protection does the architecture provide between the two types of clients and the bookstore service?

      The client and the server can only communicate via sending HTTP messages to do actions like read and write.

   3. How is enforced modularity affected when we run clients and services locally in the same JVM, as possible through our test cases?

      When running locally the test case first new a certainbookstore object and run functions directly via the object, and it's still strongly modular.

3. 

   1. Is there a naming service in the architecture? If so, what is its functionality?

      Yes, there's a Message Handler that can handle different names and lead them to the correct function.

   2. Describe the naming mechanism that allows clients to discover and communicate with services.

      First, it sets up an HTTP proxy to communicate with the server, and inside the proxy, some functions will send requests to different URLs, and whenever the server receives those requests, it will run different functions with the help of a naming service.

4. We have studied three types of RPC semantics: at-least-once, at-most-once, and exactly-once semantics. What RPC semantics is implemented in the architecture? Justify your answer.

   In the architecture, at-most-once semantics are used. If the user inputs some books that include valid and invalid, the proxy will consider it an invalid group and won't change the data so that it will be executed at most once.

5.  

   1. Is it reasonable to use web proxy servers with the architecture of Figure 1?

      Yes

   2. If so, explain why and describe in between which components these proxy servers should be deployed. If not, why not?

      The bookStore server may have a lot of users, including store users and stock managers, so with proxy servers, the number of users of the server can be scaled a lot. It needs to be deployed between those places where users have to interact with the server to enhance the security and multiple user connections.

6.  

   1. Is/are there any scalability bottleneck/s in this architecture with respect to the number of clients?

      Yes

   2. If so, where is/are the bottleneck/s? If not, why can we infinitely scale the number of clients accessing this service?

      The server could have some bottlenecks when it's interacting with some slow resources or some resources needs to be used by different layers. If the number of clients increases those resources will reach their max amount, and the server will have a bottleneck.

7.  

   1. Would clients experience failures differently if web proxies were used in the architecture?

      Yes. If there's a failure in the server, if a web proxy is not used, the user will get the result directly from the server. As the server is crushed, the user will get an HTTP error directly. If the web proxy is used, the proxy will receive the HTTP error and it can make some changes to it and return some readable error to the user.

   2. Could caching at the web proxies be employed as a way to mask failures from clients?

      If a user wants to see a page, a cache can return the same failure each time the user posts the request.

   3. How would the use of web caching affect the semantics offered by the bookstore service?

      If a user wants to getbooks, if there's a cache that exists it may return the value. And after if the user wants to use that value there will be an error on the server.

      