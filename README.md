<h1><b>WordPress XML-RPC BruteForce Tool</b></h1>

<img width="517" alt="Example" src="https://user-images.githubusercontent.com/55408375/184479953-b43222f0-947f-4041-aed1-5132649149d9.png">

With the use of this tool you will be able, given a username and a password dictionary, to bruteforce any given WordPress website through the use of its XML-RPC API.

<b>Disclaimer:</b> For educational purposes only. Not intended for illegal activities. The author is not responsible for any action performed by the software user.

<h2><b>Features</b></h2>

- Accepts SOCKS 4/5 Proxies.
- Allows to set a Custom Delay to be used when Rate-Limited.
- Allows Custom URLs (to use when the XMLRCP.php file has been moved or renamed).
- Fast and Reliable (100% Java).
- Supports any password dictionary formatted with one password per line.

Example of a password dictionary: <br>
<img width="243" alt="Sample-Dictionary" src="https://user-images.githubusercontent.com/55408375/184492840-ab629a58-402b-46d6-a61c-b844dbd1c01b.png">

<h2><b>Installation</b></h2>

Download the latest release from <a href="https://github.com/NicoloLazzaroni/WP-XMLRPC-BruteForce/releases">here</a>.<br>

<b>Requires Java 17.</b>

<h2><b>How to Use</b></h2>

In a shell run the program with `java -jar WordpressXMLBruteForce.jar` and configure it with your preferred parameters.

When the program finds a correct match, that is both printed in the shell and saved in a file called `LoginDetails`; you will find it in the same directory as the jar file.

If you want to run the program in proxy mode you will first have to create a file called `Proxies` in the same directory as the jar file. <br>
The proxies have to either be SOCKS 4 or 5 and the file has to be formatted with one proxy per line in the format: <br>
`IP:PORT`.

<img width="115" alt="Proxies Example" src="https://user-images.githubusercontent.com/55408375/184480351-e7126b4b-2902-412a-8c85-90dbca1e121b.png">
