#include <pcap.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/socket.h>
#include <linux/netlink.h>

#define MAX_PAYLOAD 1024  // Maximum payload size

// Callback function for netlink messages
void nl_callback(struct nlmsghdr *nlmsg, void *arg) {
    struct nlattr *nla;
    int hdrlen, attrlen;
    char *payload;

    hdrlen = sizeof(struct nlmsghdr);
    nla = (struct nlattr *) NLMSG_DATA(nlmsg);
    attrlen = NLMSG_PAYLOAD(nlmsg, hdrlen);
    payload = (char *) NLA_DATA(nla);

    if (nla->nla_type == NETLINK_ADD_LISTENER) {
        printf("New port opened: %s\n", payload);
    }
}

int main(int argc, char *argv[]) {
    pcap_t *handle;
    char errbuf[PCAP_ERRBUF_SIZE];
    struct bpf_program fp;
    char filter_exp[] = "tcp";
    bpf_u_int32 net;
    struct nl_sock *nl_sock;
    struct nlmsgerr err;
    char *nlmsg;
    struct nlmsghdr *nlhdr;
    struct nlattr *nla;

    // Initialize netlink socket
    nl_sock = nl_socket_alloc();
    if (nl_sock == NULL) {
        printf("Failed to allocate netlink socket: %s\n", strerror(errno));
        return 1;
    }

    // Bind to netlink socket
    if (nl_connect(nl_sock, NETLINK_INET_DIAG) < 0) {
        printf("Failed to bind to netlink socket: %s\n", strerror(errno));
        return 1;
    }

    // Add listener to netlink socket
    nlmsg = malloc(NLMSG_SPACE(MAX_PAYLOAD));
    memset(nlmsg, 0, NLMSG_SPACE(MAX_PAYLOAD));
    nlhdr = (struct nlmsghdr *) nlmsg;
    nla = (struct nlattr *) NLMSG_DATA(nlhdr);
    nlhdr->nlmsg_len = NLMSG_LENGTH(sizeof(struct nlattr));
    nlhdr->nlmsg_type = SOCK_DIAG_BY_FAMILY;
    nlhdr->nlmsg_flags = NLM_F_REQUEST | NLM_F_ROOT;
    nla->nla_type = NETLINK_ADD_LISTENER;
    nla->nla_len = strlen("inet") + 1 + sizeof(struct nlattr);
    strcpy(NLA_DATA(nla), "inet");
    nlhdr->nlmsg_len += nla->nla_len;
    if (nl_send_auto_complete(nl_sock, nlmsg) < 0) {
        printf("Failed to add netlink listener: %s\n", strerror(errno));
        return 1;
    }

    // Free netlink message
    free(nlmsg);

    // Open pcap handle
    handle = pcap_open_live("any", BUFSIZ, 1, 1000, errbuf);
    if (handle == NULL) {
        printf("Failed to open pcap handle: %s\n", errbuf);
        return 1;
    }

    // Compile and apply filter
    if (pcap_compile(handle, &fp, filter_exp, 0, net) == -1) {
        printf("Failed to compile filter: %s\n", pcap_geterr(handle));
        return 1;
    }
    if (pcap_setfilter(handle, &fp) ==-1) {
printf("Failed to set filter: %s\n", pcap_geterr(handle));
return 1;
}
// Set non-blocking mode
if (pcap_setnonblock(handle, 1, errbuf) == -1) {
    printf("Failed to set non-blocking mode: %s\n", errbuf);
    return 1;
}

// Start capturing packets
while (1) {
    struct pcap_pkthdr *header;
    const u_char *packet;

    packet = pcap_next(handle, &header);
    if (packet != NULL) {
        // Process packet here
    }

    // Check netlink socket for new ports
    if (nl_recvmsgs_default(nl_sock) < 0) {
        printf("Failed to receive netlink message: %s\n", strerror(errno));
        return 1;
    }
}

// Close pcap handle
pcap_close(handle);

return 0;
}
