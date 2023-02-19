package main

import (
    "fmt"
    "net/http"
    "net/http/httputil"
    "net/url"
    "time"
)

// HTTPProxy is a HTTP proxy server that intercepts HTTP and HTTPS requests.
type HTTPProxy struct {
    target       *url.URL
    proxyHandler *httputil.ReverseProxy
}

// NewHTTPProxy creates a new instance of HTTPProxy.
func NewHTTPProxy(targetURL string) (*HTTPProxy, error) {
    target, err := url.Parse(targetURL)
    if err != nil {
        return nil, err
    }

    proxy := &HTTPProxy{
        target: target,
        proxyHandler: httputil.NewSingleHostReverseProxy(target),
    }

    // Modify the Director function to intercept HTTPS requests.
    proxy.proxyHandler.Director = func(req *http.Request) {
        req.URL.Scheme = proxy.target.Scheme
        req.URL.Host = proxy.target.Host
        req.URL.Path = req.URL.Path
        req.Host = proxy.target.Host
    }

    return proxy, nil
}

// ServeHTTP handles the HTTP requests and adds the time headers.
func (p *HTTPProxy) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    startTime := time.Now()

    // Intercept the request and add the "X-Received-Time" header.
    r.Header.Add("X-Received-Time", startTime.Format(time.RFC3339))

    // Serve the request using the standard HTTP handler.
    p.proxyHandler.ServeHTTP(w, r)

    // Add the "X-Response-Time" header to the response.
    endTime := time.Now()
    w.Header().Add("X-Response-Time", endTime.Format(time.RFC3339))
}

func main() {
    // Create a new HTTP proxy server listening on port 8080.
    proxy, err := NewHTTPProxy("http://localhost:8000")
    if err != nil {
        fmt.Printf("Error creating proxy server: %v", err)
        return
    }

    fmt.Println("Starting proxy server on port 8080...")
    err = http.ListenAndServe(":8080", proxy)
    if err != nil {
        fmt.Printf("Error starting proxy server: %v", err)
    }
}
