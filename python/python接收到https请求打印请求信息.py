# openssl req -newkey rsa:2048 -nodes -keyout server.key -x509 -days 365 -out server.crt
# cat server.crt server.key > server.pem

import http.server
import http.client
import ssl

class MyHTTPHandler(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        self.handle_request('GET')

    def do_POST(self):
        self.handle_request('POST')

    def do_PUT(self):
        self.handle_request('PUT')

    def do_DELETE(self):
        self.handle_request('DELETE')

    def handle_request(self, method):
        content_length = int(self.headers.get('Content-Length', 0))
        request_body = self.rfile.read(content_length)

        print(f'Received {method} request with url: {self.path}')
        print(f'Request headers: {self.headers}')
        print(f'Request body: {request_body.decode("utf-8")}')

        self.send_response(200)
        self.end_headers()
        self.wfile.write(b'Received request')

httpd = http.server.HTTPServer(('localhost', 8000), MyHTTPHandler)

httpd.socket = ssl.wrap_socket(httpd.socket, certfile='./server.pem', server_side=True)

httpd.serve_forever()
