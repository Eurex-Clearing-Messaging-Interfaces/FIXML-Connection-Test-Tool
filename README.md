[![CircleCI](https://circleci.com/gh/Eurex-Clearing-Messaging-Interfaces/FIXML-Connection-Test-Tool.svg?style=shield)](https://circleci.com/gh/Eurex-Clearing-Messaging-Interfaces/FIXML-Connection-Test-Tool)

# FIXML Connection Test Tool

## Download
Releases can be downloaded from the [release section](https://github.com/Eurex-Clearing-Messaging-Interfaces/FIXML-Connection-Test-Tool/releases/tag/v1.5.1)
- Latest release v1.5.1: [ZIP](https://github.com/Eurex-Clearing-Messaging-Interfaces/FIXML-Connection-Test-Tool/releases/download/v1.5.1/fixml-connection-test-tool.zip)

## Overview

FIXML Connection Test Tool is a simple Java tool, which provides two basic functionalities:

1. Receiving a broadcast message: Connects to the broker and tries to receive a single broadcast message.
2. Sending a request message and receiving a response: Connects to the broker and registers a response queue. Sends single request message and tries to receive a response.

It was designed as a connection tool for members, since the Java SSL debugging capabilities are very useful for analysis of issues related to SSL certificates. It was primarily designed for FIXML interface but can be used also for FpML (see example how to specify FpML stream name).

## Requirements

The FIXML Connection Test Tool is developed using Java 11. The tool has been tested on both Windows and Linux - it is expected to run without issues on all platforms with official Java support (e.g. Solaris). The tool doesn't have any kind of GUI. That allows its execution on machines without a graphical user interface.

:information_source: **Note:** If the tool needs to be run on a machine with Java 8 use `-P java-8` parameter during the build: e.g. `mvn clean package -P java-8`

## Used API

The FIXML Connection Test Tool is based on Java API from the Apache Qpid project ( http://qpid.apache.org ), version 1.6.0 (0.61.0 in case of Java 8). All necessary components are distributed as part of the ZIP file in the "lib" subdirectory. 
The code used in the FIXML Connection Test Tool is described in the Eurex Clearing FIXML Interface specification, Volume 2-B: AMQP Programming Guide, which is available in the document section of the Eurex website ( https://www.eurexclearing.com/clearing-en/technology/c7/system-documentation-c7/System-documentation-31378?frag=246932 ).

## Installation

Unzip the archive content into a destination directory. Under Linux, it is necessary to add the "execute" flag to broadcast-receiver.sh and request-responder.sh file. This can be achieved by typing:
- chmod a+x broadcast-receiver.sh
- chmod a+x request-responder.sh

## Usage

From the unzipped archive execute either `./broadcast-receiver.sh` (Linux) / `broadcast-receiver.bat` (Windows) for testing the broadcast functionality or `./request-responder.sh` (Linux) / `request-responder.bat` (Windows) for testing the request/response functionality. This will print all the available command line parameters. Parameters with an additional value may be written as `--param=value` or `--param value`.

:information_source: _**Note:** If the tool is used under Windows, use normal slashes `/` when referencing the full or relative path to certificates. Do not use backslashes `\`:_
  - :heavy_check_mark: `C:/<directory>/CBKFR_CBKFRALMMACC1.keystore` - OK
  - :x: `C:\<directory>\CBKFR_CBKFRALMMACC1.keystore` - Not OK

### Mandatory parameters
- **--account <account name>** Member account ID
- **--host <host name/IP addr>** AMQP broker host name or IP address
- **--port <port number>** AMQP SSL broker port number
- **--truststore <store file>** JKS store file with AMQP broker public keys(s)
- **--truststore-password <password>** Password protecting the truststore
- **--keystore <store file>** JKS store file with member account private key(s)
- **--keystore-password <password>** Password protecting the keystore

### Optional parameters
- **--key-alias <alias>** Alias of the private key to be used (default: same as account name in lower-case)
- **--log-level <level>** Logging level (default: INFO; other possibilities: ERROR, WARN, DEBUG, TRACE)
- **--ssl-debug** Enable detailed SSL logging (default: off)
- **--verify-hostname** Verify remote host identity (default: off)
- **--timeout <time-out in ms>** How long to wait for a message (default: 1000 ms)
- **--connection-check-timeout <time-out in ms>** How long to wait for a connection check (default: 10000 ms)
- **--message-count <message count>** How many messages will be processed (default: 1)

### Optional parameters for broadcast-receiver
- **--stream <stream name>** Broadcast stream to read from (default: TradeConfirmation)

### Optional parameters for request-responder
- **--msg-content <message content>** Content of the message to be sent. Default: 'FIXML Connection Test Tool testing message'
- **--msg-content-file <message content file>** File name the content of the message should be read from. To read
                                              from standard input use 'STDIN' name.

:information_source: _**Note:** On some systems the timeouts might be lowered. 

## Example
    ./broadcast-receiver.sh --account=CBKFR_CBKFRALMMACC1 --host=ecag-fixml-simu1.deutsche-boerse.com --port=10170 --keystore=CBKFR_CBKFRALMMACC1.keystore --keystore-password=123456 --truststore=truststore --truststore-password=123456 --verify-hostname

### Output

Depending on the chosen log level, the output will contain more or less informational messages.

#### Message received
For the default log level, the following kind of output is expected in case of a successfully received message:

    2013-06-04 09:29:04 +0200 [main] INFO org.apache.qpid.jndi.PropertiesFileInitialContextFactory - No Provider URL specified.
	2013-06-04 09:29:05 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] WARN org.apache.qpid.transport.network.security.ssl.SSLUtil - Exception received while trying to verify hostname
	2013-06-04 09:29:05 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] INFO org.apache.qpid.client.security.DynamicSaslRegistrar - Additional SASL providers successfully registered.
	2013-06-04 09:29:05 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] INFO org.apache.qpid.client.security.CallbackHandlerRegistry - Callback handlers available for SASL mechanisms: [EXTERNAL, GSSAPI, CRAM-MD5-HASHED, CRAM-MD5, AMQPLAIN, PLAIN, ANONYMOUS]
	2013-06-04 09:29:05 +0200 [main] INFO org.apache.qpid.client.AMQConnection - Connection 1 now connected from /172.16.149.209:54748 to cbgc03/172.16.153.12:10128
	2013-06-04 09:29:05 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Connected
	2013-06-04 09:29:05 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Broadcast consumer created
	2013-06-04 09:29:05 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Byte message received, length = 53, content:
	Here is the content (payload) of the received message
	2013-06-04 09:29:05 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Disconnected
    
#### No message available

	2013-06-04 09:30:18 +0200 [main] INFO org.apache.qpid.jndi.PropertiesFileInitialContextFactory - No Provider URL specified.
	2013-06-04 09:30:18 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] WARN org.apache.qpid.transport.network.security.ssl.SSLUtil - Exception received while trying to verify hostname
	2013-06-04 09:30:18 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] INFO org.apache.qpid.client.security.DynamicSaslRegistrar - Additional SASL providers successfully registered.
	2013-06-04 09:30:18 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] INFO org.apache.qpid.client.security.CallbackHandlerRegistry - Callback handlers available for SASL mechanisms: [EXTERNAL, GSSAPI, CRAM-MD5-HASHED, CRAM-MD5, AMQPLAIN, PLAIN, ANONYMOUS]
	2013-06-04 09:30:18 +0200 [main] INFO org.apache.qpid.client.AMQConnection - Connection 1 now connected from /172.16.149.209:55332 to cbgc03/172.16.153.12:10128
	2013-06-04 09:30:18 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Connected
	2013-06-04 09:30:18 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Broadcast consumer created
	2013-06-04 09:30:19 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - No message received
	2013-06-04 09:30:19 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Disconnected

## SSL debugging mode

This mode prints detailed information about the SSL handshake in addition.

	2013-06-04 09:32:27 +0200 [main] INFO org.apache.qpid.jndi.PropertiesFileInitialContextFactory - No Provider URL specified.
	adding as trusted cert:
	  Subject: CN=cbgc03
	  Issuer:  CN=cbgc03
	  Algorithm: RSA; Serial number: 0x9bbfece4
	  Valid from Fri May 31 10:28:43 CEST 2013 until Tue May 31 10:28:43 CEST 2016
	
	***
	found key for : cbkfr_testcalmmacc1
	chain [0] = [
	[
	  Version: V3
	  Subject: CN=CBKFR_TESTCALMMACC1
	  Signature Algorithm: SHA512withRSA, OID = 1.2.840.113549.1.1.13
	
	  Key:  Sun RSA public key, 2048 bits
	  modulus: 22412803298255723208115517821912247807812232417072861563478999429122692494391262513027600163334835036439624188181192577590706389895929370857936059978787002496753349506950862064060733257452863750443209573922856989139949572329492169622187647399118462361559700823592644793100748647953615327193202979699368025379059606980126600331114340545203696195340216653278243181534948227383465972539213470485443506451413502403843053344327189843329410012345333295934377484377303683181375498725124369960285244036396559925189575114076577206549341553078241239185054920028658961562016785245887892584549324515131362076955047250903642321327
	  public exponent: 65537
	  Validity: [From: Fri May 31 10:28:49 CEST 2013,
	               To: Mon May 30 10:28:49 CEST 2016]
	  Issuer: CN=CBKFR_TESTCALMMACC1
	  SerialNumber: [    51a85f41]
	
	]
	  Algorithm: [SHA512withRSA]
	  Signature:
	0000: 91 E9 D8 99 3C 2F 99 89   1D C3 31 76 A7 72 9D D3  ....</....1v.r..
	0010: 77 C7 53 C0 6E 9B E8 11   4F 76 39 EC 2F 92 C1 6A  w.S.n...Ov9./..j
	0020: CC E2 89 17 1A 42 E8 A0   81 0E 88 36 F3 88 22 34  .....B.....6.."4
	0030: 02 F2 BA 49 38 E4 26 00   87 42 DA 2F 2F AA D8 F6  ...I8.&..B.//...
	0040: 86 C6 CF FD 05 2D 25 E0   66 3A 7E 78 A1 46 66 22  .....-%.f:.x.Ff"
	0050: B6 0C 54 21 F3 3F 9F 02   2E 6E A2 B9 C3 40 33 37  ..T!.?...n...@37
	0060: 3C 43 D5 3C F0 9F 80 20   40 DB CE 7A 6C 52 9F 02  <C.<... @..zlR..
	0070: 55 67 A7 57 75 F4 91 24   F7 23 C6 8A 13 2C 52 0C  Ug.Wu..$.#...,R.
	0080: B4 43 2C D6 D6 85 F9 D3   4E 58 8B CD 2F B4 F7 BA  .C,.....NX../...
	0090: 81 B5 13 BF 18 C1 B4 3B   00 A0 AD 79 B1 12 AE 75  .......;...y...u
	00A0: 43 93 14 F1 B7 78 4E FB   A7 58 55 7E 8D BD DC 38  C....xN..XU....8
	00B0: CD B3 60 47 8D 00 24 F3   D9 B8 29 3D EA 37 07 6F  ..`G..$...)=.7.o
	00C0: 8E BD 7B 9F DC 0F EA 6F   9E A1 8C AF 1B CD 3E 71  .......o......>q
	00D0: 90 DE D9 3C 0C 27 7E 2A   AF 21 C8 E1 3B 44 43 95  ...<.'.*.!..;DC.
	00E0: CD B7 B8 23 1F 7B 09 FC   F7 15 96 4F C5 CE B6 E1  ...#.......O....
	00F0: E1 75 9C 4F D6 5F 20 E1   B2 38 CC 9F FD B2 01 DA  .u.O._ ..8......
	
	]
	***
	trigger seeding of SecureRandom
	done seeding SecureRandom
	Using SSLEngineImpl.
	Allow unsafe renegotiation: false
	Allow legacy hello messages: true
	Is initial handshake: true
	Is secure renegotiation: false
	%% No cached client session
	*** ClientHello, TLSv1
	RandomCookie:  GMT: 1353488396 bytes = { 70, 71, 31, 104, 89, 61, 15, 244, 74, 56, 148, 75, 179, 193, 105, 85, 211, 49, 116, 127, 110, 94, 88, 67, 48, 228, 229, 164 }
	Session ID:  {}
	Cipher Suites: [SSL_RSA_WITH_RC4_128_MD5, SSL_RSA_WITH_RC4_128_SHA, TLS_RSA_WITH_AES_128_CBC_SHA, TLS_DHE_RSA_WITH_AES_128_CBC_SHA, TLS_DHE_DSS_WITH_AES_128_CBC_SHA, SSL_RSA_WITH_3DES_EDE_CBC_SHA, SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA, SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA, SSL_RSA_WITH_DES_CBC_SHA, SSL_DHE_RSA_WITH_DES_CBC_SHA, SSL_DHE_DSS_WITH_DES_CBC_SHA, SSL_RSA_EXPORT_WITH_RC4_40_MD5, SSL_RSA_EXPORT_WITH_DES40_CBC_SHA, SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA, SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA, TLS_EMPTY_RENEGOTIATION_INFO_SCSV]
	Compression Methods:  { 0 }
	***
	main, WRITE: TLSv1 Handshake, length = 75
	main, WRITE: SSLv2 client hello message, length = 101
	IoReceiver - cbgc03/172.16.153.12:10128, READ: TLSv1 Handshake, length = 804
	*** ServerHello, TLSv1
	RandomCookie:  GMT: 1353488396 bytes = { 116, 109, 29, 146, 178, 92, 73, 64, 168, 144, 196, 189, 79, 244, 201, 194, 50, 167, 18, 253, 35, 127, 32, 51, 150, 9, 4, 137 }
	Session ID:  {24, 184, 93, 88, 212, 94, 70, 254, 166, 103, 49, 190, 160, 24, 29, 145, 89, 63, 77, 201, 149, 61, 193, 8, 10, 140, 12, 240, 103, 81, 29, 17}
	Cipher Suite: SSL_RSA_WITH_RC4_128_MD5
	Compression Method: 0
	Extension renegotiation_info, renegotiated_connection: <empty>
	***
	%% Created:  [Session-1, SSL_RSA_WITH_RC4_128_MD5]
	** SSL_RSA_WITH_RC4_128_MD5
	*** Certificate chain
	chain [0] = [
	[
	  Version: V3
	  Subject: CN=cbgc03
	  Signature Algorithm: SHA512withRSA, OID = 1.2.840.113549.1.1.13
	
	  Key:  Sun RSA public key, 2048 bits
	  modulus: 21298009436244951438101892264450788291450072859010523795243495795733316857538207920499535560548035626280964329474327691852897564951601461086757741971486692455522321012083422063897622089480639008862173554827297210027317686957220527780415899696582864049659807225506929977272323451760393829585455615797720934992730594105761225978543027048735761525112329721205612209742213439901423513240453434674523645541038076461437153035511326022259400496549023127059727548520444501463248836728742802214089222529700303537736711087733728536709811009955291703725613384331887389146649360330006910431141713727318595552753976009911556061859
	  public exponent: 65537
	  Validity: [From: Fri May 31 10:28:43 CEST 2013,
	               To: Tue May 31 10:28:43 CEST 2016]
	  Issuer: CN=cbgc03
	  SerialNumber: [    9bbfece4]
	
	]
	  Algorithm: [SHA512withRSA]
	  Signature:
	0000: 3E 6C 59 80 D3 56 4D 69   C0 73 97 73 81 24 F7 09  >lY..VMi.s.s.$..
	0010: F3 C1 A4 2D C4 5A 52 28   62 43 BE 97 BB BE 65 9C  ...-.ZR(bC....e.
	0020: B4 0A 63 83 B9 CE A4 A7   8B 9D 68 62 9A 7D E7 DB  ..c.......hb....
	0030: 58 B3 A1 19 3B 5F EB BF   72 40 79 4C 46 B3 F8 A3  X...;_..r@yLF...
	0040: 1F 46 C4 E4 ED E3 27 EF   70 D6 5D EB 47 00 72 2F  .F....'.p.].G.r/
	0050: 64 33 D2 99 B0 25 1E 7A   DA 12 D5 8E 30 0B 9A CA  d3...%.z....0...
	0060: 9C 47 3C CD 73 5F B9 A4   A0 62 C8 B9 93 79 21 EF  .G<.s_...b...y!.
	0070: 5D E1 00 3F 3B 61 AB 6C   C8 1E 6B F3 55 B3 4C 14  ]..?;a.l..k.U.L.
	0080: 6C A7 7C 00 33 0E 0D 0C   21 85 DB E4 84 11 9A 99  l...3...!.......
	0090: 72 E8 64 55 BF CD 18 2D   3F 39 77 98 95 DC 43 53  r.dU...-?9w...CS
	00A0: 9B E6 A1 20 C2 00 67 C1   B4 81 03 BE 93 8A 07 15  ... ..g.........
	00B0: 6F 0B D9 70 10 19 BE 14   E8 04 68 42 2C 39 91 4D  o..p......hB,9.M
	00C0: 16 0E 4A 08 02 69 C3 8A   7E C4 ED 47 8E 01 FB 4F  ..J..i.....G...O
	00D0: 48 BD 5A 44 9D 0B EF 74   00 1A 7B 73 15 FF 7E 4D  H.ZD...t...s...M
	00E0: F4 13 E1 0E 82 8D 46 32   2D B9 C5 C3 73 47 AE A3  ......F2-...sG..
	00F0: 73 86 62 94 F9 FD C7 3C   9F EA C7 4E 8E CC A9 68  s.b....<...N...h
	
	]
	***
	Found trusted certificate:
	[
	[
	  Version: V3
	  Subject: CN=cbgc03
	  Signature Algorithm: SHA512withRSA, OID = 1.2.840.113549.1.1.13
	
	  Key:  Sun RSA public key, 2048 bits
	  modulus: 21298009436244951438101892264450788291450072859010523795243495795733316857538207920499535560548035626280964329474327691852897564951601461086757741971486692455522321012083422063897622089480639008862173554827297210027317686957220527780415899696582864049659807225506929977272323451760393829585455615797720934992730594105761225978543027048735761525112329721205612209742213439901423513240453434674523645541038076461437153035511326022259400496549023127059727548520444501463248836728742802214089222529700303537736711087733728536709811009955291703725613384331887389146649360330006910431141713727318595552753976009911556061859
	  public exponent: 65537
	  Validity: [From: Fri May 31 10:28:43 CEST 2013,
	               To: Tue May 31 10:28:43 CEST 2016]
	  Issuer: CN=cbgc03
	  SerialNumber: [    9bbfece4]
	
	]
	  Algorithm: [SHA512withRSA]
	  Signature:
	0000: 3E 6C 59 80 D3 56 4D 69   C0 73 97 73 81 24 F7 09  >lY..VMi.s.s.$..
	0010: F3 C1 A4 2D C4 5A 52 28   62 43 BE 97 BB BE 65 9C  ...-.ZR(bC....e.
	0020: B4 0A 63 83 B9 CE A4 A7   8B 9D 68 62 9A 7D E7 DB  ..c.......hb....
	0030: 58 B3 A1 19 3B 5F EB BF   72 40 79 4C 46 B3 F8 A3  X...;_..r@yLF...
	0040: 1F 46 C4 E4 ED E3 27 EF   70 D6 5D EB 47 00 72 2F  .F....'.p.].G.r/
	0050: 64 33 D2 99 B0 25 1E 7A   DA 12 D5 8E 30 0B 9A CA  d3...%.z....0...
	0060: 9C 47 3C CD 73 5F B9 A4   A0 62 C8 B9 93 79 21 EF  .G<.s_...b...y!.
	0070: 5D E1 00 3F 3B 61 AB 6C   C8 1E 6B F3 55 B3 4C 14  ]..?;a.l..k.U.L.
	0080: 6C A7 7C 00 33 0E 0D 0C   21 85 DB E4 84 11 9A 99  l...3...!.......
	0090: 72 E8 64 55 BF CD 18 2D   3F 39 77 98 95 DC 43 53  r.dU...-?9w...CS
	00A0: 9B E6 A1 20 C2 00 67 C1   B4 81 03 BE 93 8A 07 15  ... ..g.........
	00B0: 6F 0B D9 70 10 19 BE 14   E8 04 68 42 2C 39 91 4D  o..p......hB,9.M
	00C0: 16 0E 4A 08 02 69 C3 8A   7E C4 ED 47 8E 01 FB 4F  ..J..i.....G...O
	00D0: 48 BD 5A 44 9D 0B EF 74   00 1A 7B 73 15 FF 7E 4D  H.ZD...t...s...M
	00E0: F4 13 E1 0E 82 8D 46 32   2D B9 C5 C3 73 47 AE A3  ......F2-...sG..
	00F0: 73 86 62 94 F9 FD C7 3C   9F EA C7 4E 8E CC A9 68  s.b....<...N...h
	
	]
	*** CertificateRequest
	Cert Types: RSA, DSS
	Cert Authorities:
	<CN=qpid_dummy>
	*** ServerHelloDone
	*** Certificate chain
	chain [0] = [
	[
	  Version: V3
	  Subject: CN=CBKFR_TESTCALMMACC1
	  Signature Algorithm: SHA512withRSA, OID = 1.2.840.113549.1.1.13
	
	  Key:  Sun RSA public key, 2048 bits
	  modulus: 22412803298255723208115517821912247807812232417072861563478999429122692494391262513027600163334835036439624188181192577590706389895929370857936059978787002496753349506950862064060733257452863750443209573922856989139949572329492169622187647399118462361559700823592644793100748647953615327193202979699368025379059606980126600331114340545203696195340216653278243181534948227383465972539213470485443506451413502403843053344327189843329410012345333295934377484377303683181375498725124369960285244036396559925189575114076577206549341553078241239185054920028658961562016785245887892584549324515131362076955047250903642321327
	  public exponent: 65537
	  Validity: [From: Fri May 31 10:28:49 CEST 2013,
	               To: Mon May 30 10:28:49 CEST 2016]
	  Issuer: CN=CBKFR_TESTCALMMACC1
	  SerialNumber: [    51a85f41]
	
	]
	  Algorithm: [SHA512withRSA]
	  Signature:
	0000: 91 E9 D8 99 3C 2F 99 89   1D C3 31 76 A7 72 9D D3  ....</....1v.r..
	0010: 77 C7 53 C0 6E 9B E8 11   4F 76 39 EC 2F 92 C1 6A  w.S.n...Ov9./..j
	0020: CC E2 89 17 1A 42 E8 A0   81 0E 88 36 F3 88 22 34  .....B.....6.."4
	0030: 02 F2 BA 49 38 E4 26 00   87 42 DA 2F 2F AA D8 F6  ...I8.&..B.//...
	0040: 86 C6 CF FD 05 2D 25 E0   66 3A 7E 78 A1 46 66 22  .....-%.f:.x.Ff"
	0050: B6 0C 54 21 F3 3F 9F 02   2E 6E A2 B9 C3 40 33 37  ..T!.?...n...@37
	0060: 3C 43 D5 3C F0 9F 80 20   40 DB CE 7A 6C 52 9F 02  <C.<... @..zlR..
	0070: 55 67 A7 57 75 F4 91 24   F7 23 C6 8A 13 2C 52 0C  Ug.Wu..$.#...,R.
	0080: B4 43 2C D6 D6 85 F9 D3   4E 58 8B CD 2F B4 F7 BA  .C,.....NX../...
	0090: 81 B5 13 BF 18 C1 B4 3B   00 A0 AD 79 B1 12 AE 75  .......;...y...u
	00A0: 43 93 14 F1 B7 78 4E FB   A7 58 55 7E 8D BD DC 38  C....xN..XU....8
	00B0: CD B3 60 47 8D 00 24 F3   D9 B8 29 3D EA 37 07 6F  ..`G..$...)=.7.o
	00C0: 8E BD 7B 9F DC 0F EA 6F   9E A1 8C AF 1B CD 3E 71  .......o......>q
	00D0: 90 DE D9 3C 0C 27 7E 2A   AF 21 C8 E1 3B 44 43 95  ...<.'.*.!..;DC.
	00E0: CD B7 B8 23 1F 7B 09 FC   F7 15 96 4F C5 CE B6 E1  ...#.......O....
	00F0: E1 75 9C 4F D6 5F 20 E1   B2 38 CC 9F FD B2 01 DA  .u.O._ ..8......
	
	]
	***
	*** ClientKeyExchange, RSA PreMasterSecret, TLSv1
	main, WRITE: TLSv1 Handshake, length = 972
	SESSION KEYGEN:
	PreMaster Secret:
	0000: 03 01 F0 84 8B B2 13 D3   53 AB 13 63 F5 3F 3C 73  ........S..c.?<s
	0010: 6F 52 07 8A CE 85 76 EB   98 5A 34 03 BC CE 68 AF  oR....v..Z4...h.
	0020: 26 17 24 BC 45 10 35 67   E1 06 60 99 60 18 AE 8C  &.$.E.5g..`.`...
	CONNECTION KEYGEN:
	Client Nonce:
	0000: 51 AD 98 0C 46 47 1F 68   59 3D 0F F4 4A 38 94 4B  Q...FG.hY=..J8.K
	0010: B3 C1 69 55 D3 31 74 7F   6E 5E 58 43 30 E4 E5 A4  ..iU.1t.n^XC0...
	Server Nonce:
	0000: 51 AD 98 0C 74 6D 1D 92   B2 5C 49 40 A8 90 C4 BD  Q...tm...\I@....
	0010: 4F F4 C9 C2 32 A7 12 FD   23 7F 20 33 96 09 04 89  O...2...#. 3....
	Master Secret:
	0000: 8C 6B F4 4B 34 47 07 B6   89 27 24 3D 41 CC 97 9B  .k.K4G...'$=A...
	0010: B2 FF 12 FD BB CB 2A 97   BA ED F9 E7 42 7D 7B DD  ......*.....B...
	0020: 55 77 8A 66 B1 42 1F 56   6A 71 A8 68 45 E9 68 55  Uw.f.B.Vjq.hE.hU
	Client MAC write Secret:
	0000: 08 C3 85 09 FE D5 EA B4   EB 0F DA FD 34 44 63 07  ............4Dc.
	Server MAC write Secret:
	0000: C6 13 3B 71 20 AC 06 FF   CC 10 D9 2C 02 02 59 9C  ..;q ......,..Y.
	Client write key:
	0000: 97 E9 26 F2 62 0B 63 06   F7 56 7C 24 A7 B1 4A 94  ..&.b.c..V.$..J.
	Server write key:
	0000: 01 3E 54 32 24 EE 82 7F   51 39 62 FE 23 A1 B4 00  .>T2$...Q9b.#...
	... no IV used for this cipher
	*** CertificateVerify
	main, WRITE: TLSv1 Handshake, length = 262
	main, WRITE: TLSv1 Change Cipher Spec, length = 1
	*** Finished
	verify_data:  { 124, 13, 195, 49, 131, 190, 247, 178, 35, 148, 224, 24 }
	***
	main, WRITE: TLSv1 Handshake, length = 32
	2013-06-04 09:32:28 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] WARN org.apache.qpid.transport.network.security.ssl.SSLUtil - Exception received while trying to verify hostname
	IoReceiver - cbgc03/172.16.153.12:10128, READ: TLSv1 Change Cipher Spec, length = 1
	IoReceiver - cbgc03/172.16.153.12:10128, READ: TLSv1 Handshake, length = 32
	*** Finished
	verify_data:  { 144, 8, 188, 12, 3, 96, 101, 140, 8, 175, 1, 83 }
	***
	%% Cached client session: [Session-1, SSL_RSA_WITH_RC4_128_MD5]
	main, WRITE: TLSv1 Application Data, length = 8
	2013-06-04 09:32:28 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] INFO org.apache.qpid.client.security.DynamicSaslRegistrar - Additional SASL providers successfully registered.
	2013-06-04 09:32:28 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] INFO org.apache.qpid.client.security.CallbackHandlerRegistry - Callback handlers available for SASL mechanisms: [EXTERNAL, GSSAPI, CRAM-MD5-HASHED, CRAM-MD5, AMQPLAIN, PLAIN, ANONYMOUS]
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Application Data, length = 12
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Application Data, length = 312
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Application Data, length = 12
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Application Data, length = 10
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Application Data, length = 12
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Application Data, length = 5
	2013-06-04 09:32:28 +0200 [main] INFO org.apache.qpid.client.AMQConnection - Connection 1 now connected from /172.16.149.209:56352 to cbgc03/172.16.153.12:10128
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 42
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 8
	2013-06-04 09:32:28 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Connected
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 16
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 54
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 4
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 6
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Application Data, length = 12
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Application Data, length = 14
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 99
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 9
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 13
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 6
	2013-06-04 09:32:28 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Broadcast consumer created
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 13
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 13
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 6
	Dispatcher-0-Conn-1, WRITE: TLSv1 Application Data, length = 12
	Dispatcher-0-Conn-1, WRITE: TLSv1 Application Data, length = 16
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 14
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 16
	2013-06-04 09:32:28 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Byte message received, length = 53, content:
	Here is the content (payload) of the received message
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 6
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 8
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 42
	main, WRITE: TLSv1 Application Data, length = 12
	main, WRITE: TLSv1 Application Data, length = 6
	IoReceiver - cbgc03/172.16.153.12:10128, called closeOutbound()
	IoReceiver - cbgc03/172.16.153.12:10128, closeOutboundInternal()
	IoReceiver - cbgc03/172.16.153.12:10128, SEND TLSv1 ALERT:  warning, description = close_notify
	IoReceiver - cbgc03/172.16.153.12:10128, WRITE: TLSv1 Alert, length = 18
	IoReceiver - cbgc03/172.16.153.12:10128, READ: TLSv1 Alert, length = 18
	IoReceiver - cbgc03/172.16.153.12:10128, RECV TLSv1 ALERT:  warning, close_notify
	IoReceiver - cbgc03/172.16.153.12:10128, closeInboundInternal()
	IoReceiver - cbgc03/172.16.153.12:10128, closeOutboundInternal()
	2013-06-04 09:32:28 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Disconnected


### Error - Account private key invalid
When the account private key supplied in keystore used for broker log-in does not match the corresponding account's
public key (kept by the broker), the following error message is expected ("bad_certificate" SSL exception):

	2013-06-04 09:40:16 +0200 [main] INFO org.apache.qpid.jndi.PropertiesFileInitialContextFactory - No Provider URL specified.
	2013-06-04 09:40:17 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] WARN org.apache.qpid.transport.network.security.ssl.SSLUtil - Exception received while trying to verify hostname
	2013-06-04 09:40:17 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] ERROR org.apache.qpid.transport.network.security.ssl.SSLReceiver - Error caught in SSLReceiver
	javax.net.ssl.SSLException: Received fatal alert: bad_certificate
		at com.sun.net.ssl.internal.ssl.Alerts.getSSLException(Alerts.java:190)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.fatal(SSLEngineImpl.java:1467)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.fatal(SSLEngineImpl.java:1435)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.recvAlert(SSLEngineImpl.java:1601)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.readRecord(SSLEngineImpl.java:1031)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.readNetRecord(SSLEngineImpl.java:845)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.unwrap(SSLEngineImpl.java:721)
		at javax.net.ssl.SSLEngine.unwrap(SSLEngine.java:607)
		at org.apache.qpid.transport.network.security.ssl.SSLReceiver.received(SSLReceiver.java:102)
		at org.apache.qpid.transport.network.security.ssl.SSLReceiver.received(SSLReceiver.java:35)
		at org.apache.qpid.transport.network.io.IoReceiver.run(IoReceiver.java:161)
		at java.lang.Thread.run(Thread.java:662)
	2013-06-04 09:40:17 +0200 [main] INFO org.apache.qpid.client.AMQConnection - Unable to connect to broker at tcp://cbgc03:10128?ssl_cert_alias='cbkfr_testcalmmacc1'&ssl_verify_hostname='true'&sasl_mechs='EXTERNAL'&ssl='true'

### Error - Broker public key invalid
When the broker public key supplied in truststore does not match the broker's private key (kept by the broker), the
following error message is expected ("Signature does not match" exception):

	2013-06-04 09:45:50 +0200 [main] INFO org.apache.qpid.jndi.PropertiesFileInitialContextFactory - No Provider URL specified.
	2013-06-04 09:45:50 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] WARN org.apache.qpid.transport.network.security.ssl.SSLUtil - Exception received while trying to verify hostname
	2013-06-04 09:45:50 +0200 [main] INFO org.apache.qpid.client.AMQConnection - Unable to connect to broker at tcp://cbgc03:10128?ssl_cert_alias='cbkfr_testcalmmacc1'&ssl_verify_hostname='true'&sasl_mechs='EXTERNAL'&ssl='true'
	org.apache.qpid.transport.SenderException: SSL, Error occurred while encrypting data
		at org.apache.qpid.transport.network.security.ssl.SSLSender.send(SSLSender.java:165)
		at org.apache.qpid.transport.network.security.ssl.SSLSender.send(SSLSender.java:35)
		at org.apache.qpid.transport.network.Disassembler.init(Disassembler.java:160)
		at org.apache.qpid.transport.network.Disassembler.init(Disassembler.java:48)
		at org.apache.qpid.transport.ProtocolHeader.delegate(ProtocolHeader.java:110)
		at org.apache.qpid.transport.network.Disassembler.send(Disassembler.java:73)
		at org.apache.qpid.transport.network.Disassembler.send(Disassembler.java:48)
		at org.apache.qpid.transport.Connection.send(Connection.java:390)
		at org.apache.qpid.transport.Connection.connect(Connection.java:244)
		at org.apache.qpid.client.AMQConnectionDelegate_0_10.makeBrokerConnection(AMQConnectionDelegate_0_10.java:221)
		at org.apache.qpid.client.AMQConnection.makeBrokerConnection(AMQConnection.java:604)
		at org.apache.qpid.client.AMQConnection.<init>(AMQConnection.java:383)
		at org.apache.qpid.client.AMQConnectionFactory.createConnection(AMQConnectionFactory.java:121)
		at de.deutscheboerse.fixml.BrokerConnector.connect(BrokerConnector.java:67)
		at de.deutscheboerse.fixml.BroadcastReceiver.connect(BroadcastReceiver.java:49)
		at de.deutscheboerse.fixml.BroadcastReceiver.main(BroadcastReceiver.java:81)
	Caused by: javax.net.ssl.SSLHandshakeException: General SSLEngine problem
		at com.sun.net.ssl.internal.ssl.Handshaker.checkThrown(Handshaker.java:1015)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.checkTaskThrown(SSLEngineImpl.java:485)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.writeAppRecord(SSLEngineImpl.java:1128)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.wrap(SSLEngineImpl.java:1100)
		at javax.net.ssl.SSLEngine.wrap(SSLEngine.java:452)
		at org.apache.qpid.transport.network.security.ssl.SSLSender.send(SSLSender.java:157)
		... 15 more
	Caused by: javax.net.ssl.SSLHandshakeException: General SSLEngine problem
		at com.sun.net.ssl.internal.ssl.Alerts.getSSLException(Alerts.java:174)
		at com.sun.net.ssl.internal.ssl.SSLEngineImpl.fatal(SSLEngineImpl.java:1528)
		at com.sun.net.ssl.internal.ssl.Handshaker.fatalSE(Handshaker.java:243)
		at com.sun.net.ssl.internal.ssl.Handshaker.fatalSE(Handshaker.java:235)
		at com.sun.net.ssl.internal.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1206)
		at com.sun.net.ssl.internal.ssl.ClientHandshaker.processMessage(ClientHandshaker.java:136)
		at com.sun.net.ssl.internal.ssl.Handshaker.processLoop(Handshaker.java:593)
		at com.sun.net.ssl.internal.ssl.Handshaker$1.run(Handshaker.java:533)
		at java.security.AccessController.doPrivileged(Native Method)
		at com.sun.net.ssl.internal.ssl.Handshaker$DelegatedTask.run(Handshaker.java:952)
		at org.apache.qpid.transport.network.security.ssl.SSLSender.doTasks(SSLSender.java:258)
		at org.apache.qpid.transport.network.security.ssl.SSLSender.send(SSLSender.java:207)
		... 15 more
	Caused by: sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: signature check failed
		at sun.security.validator.PKIXValidator.doValidate(PKIXValidator.java:289)
		at sun.security.validator.PKIXValidator.doValidate(PKIXValidator.java:263)
		at sun.security.validator.PKIXValidator.engineValidate(PKIXValidator.java:184)
		at sun.security.validator.Validator.validate(Validator.java:218)
		at com.sun.net.ssl.internal.ssl.X509TrustManagerImpl.validate(X509TrustManagerImpl.java:126)
		at com.sun.net.ssl.internal.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:209)
		at com.sun.net.ssl.internal.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:249)
		at com.sun.net.ssl.internal.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1185)
		... 22 more
	Caused by: java.security.cert.CertPathValidatorException: signature check failed
		at sun.security.provider.certpath.PKIXMasterCertPathValidator.validate(PKIXMasterCertPathValidator.java:139)
		at sun.security.provider.certpath.PKIXCertPathValidator.doValidate(PKIXCertPathValidator.java:330)
		at sun.security.provider.certpath.PKIXCertPathValidator.engineValidate(PKIXCertPathValidator.java:178)
		at java.security.cert.CertPathValidator.validate(CertPathValidator.java:250)
		at sun.security.validator.PKIXValidator.doValidate(PKIXValidator.java:275)
		... 29 more
	Caused by: java.security.SignatureException: Signature does not match.
		at sun.security.x509.X509CertImpl.verify(X509CertImpl.java:444)
		at sun.security.provider.certpath.BasicChecker.verifySignature(BasicChecker.java:133)
		at sun.security.provider.certpath.BasicChecker.check(BasicChecker.java:112)
		at sun.security.provider.certpath.PKIXMasterCertPathValidator.validate(PKIXMasterCertPathValidator.java:117)
		... 33 more

## Example
    ./request-response.sh --account=CBKFR_CBKFRALMMACC1 --host=ecag-fixml-simu1.deutsche-boerse.com --port=10170 --keystore=CBKFR_CBKFRALMMACC1.keystore --keystore-password=123456 --truststore=truststore --truststore-password=123456 --verify-hostname

### Output

Depending on the chosen log level, the output will contain more or less informational messages.

#### Message sent and received

	2013-06-04 09:49:01 +0200 [main] INFO org.apache.qpid.jndi.PropertiesFileInitialContextFactory - No Provider URL specified.
	2013-06-04 09:49:02 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] INFO org.apache.qpid.client.security.DynamicSaslRegistrar - Additional SASL providers successfully registered.
	2013-06-04 09:49:02 +0200 [IoReceiver - cbgc03/172.16.153.12:10128] INFO org.apache.qpid.client.security.CallbackHandlerRegistry - Callback handlers available for SASL mechanisms: [EXTERNAL, GSSAPI, CRAM-MD5-HASHED, CRAM-MD5, AMQPLAIN, PLAIN, ANONYMOUS]
	2013-06-04 09:49:02 +0200 [main] INFO org.apache.qpid.client.AMQConnection - Connection 1 now connected from /172.16.149.209:64234 to cbgc03/172.16.153.12:10128
	2013-06-04 09:49:02 +0200 [main] INFO de.deutscheboerse.fixml.RequestResponder - Connected
	2013-06-04 09:49:02 +0200 [main] INFO de.deutscheboerse.fixml.RequestResponder - Response consumer created
	2013-06-04 09:49:02 +0200 [main] INFO de.deutscheboerse.fixml.RequestResponder - Request producer created
	2013-06-04 09:49:02 +0200 [main] INFO de.deutscheboerse.fixml.RequestResponder - Text message received, length = 57, content:
	Responding to FIXML Connection Test Tool testing message.
	2013-06-04 09:49:02 +0200 [main] INFO de.deutscheboerse.fixml.RequestResponder - Disconnected

### Error - Account private key invalid / Error - Broker public key invalid
Error messages are identical to the same case in broadcast-receiver.sh


## Example for receiving broadcast from FpML interface
    ./broadcast-receive.sh --account=ABCFR_ABCFRALMMACC1 --host=fpmls1.eurexclearing.com --port=18575 --keystore=ABCFR_ABCFRALMMACC1.keystore --keystore-password=123456 --truststore=truststore --truststore-password=123456 --verify-hostname --stream=TradeNotification

### Output

#### Message received
For the default log level, the following kind of output is expected in case of a successfully received message:
 
	2013-06-05 11:57:20 +0200 [main] INFO de.deutscheboerse.fixml.CommonOptions - Received options: --account=ABCFR_ABCFRALMMACC1 --host=cbgc03 --keystore=dist/ABCFR_ABCFRALMMACC1.keystore --keystore-password=123456 --port=10129 --truststore=dist/truststore --truststore-password=123456 --verify-hostname --log-level=info --stream=TradeNotification
	2013-06-05 11:57:20 +0200 [main] INFO org.apache.qpid.jndi.PropertiesFileInitialContextFactory - No Provider URL specified.
	2013-06-05 11:57:21 +0200 [IoReceiver - cbgc03/172.16.153.12:10129] WARN org.apache.qpid.transport.network.security.ssl.SSLUtil - Exception received while trying to verify hostname
	2013-06-05 11:57:21 +0200 [IoReceiver - cbgc03/172.16.153.12:10129] INFO org.apache.qpid.client.security.DynamicSaslRegistrar - Additional SASL providers successfully registered.
	2013-06-05 11:57:21 +0200 [IoReceiver - cbgc03/172.16.153.12:10129] INFO org.apache.qpid.client.security.CallbackHandlerRegistry - Callback handlers available for SASL mechanisms: [EXTERNAL, GSSAPI, CRAM-MD5-HASHED, CRAM-MD5, AMQPLAIN, PLAIN, ANONYMOUS]
	2013-06-05 11:57:21 +0200 [main] INFO org.apache.qpid.client.AMQConnection - Connection 1 now connected from /172.16.149.209:59367 to cbgc03/172.16.153.12:10129
	2013-06-05 11:57:21 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Connected
	2013-06-05 11:57:21 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Broadcast consumer created
	2013-06-05 11:57:21 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Byte message received, length = 53, content:
	Here is the content (payload) of the received message
	2013-06-05 11:57:21 +0200 [main] INFO de.deutscheboerse.fixml.BroadcastReceiver - Disconnected
 
