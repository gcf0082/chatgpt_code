
#include <stdio.h>

#include <stdlib.h>

#include <string.h>

#include <errno.h>

#include <unistd.h>

#include <pthread.h>

#include <sys/types.h>

#include <sys/socket.h>

#include <linux/netlink.h>

#include <linux/taskstats.h>

#define MAX_PAYLOAD 1024

struct process_info {

    pid_t pid;

    char cmdline[1024];

};

int nlSocket;

int pipefd[2];

void *get_process_info_thread(void *arg) {

    struct process_info info;

    char path[256];

    FILE *file;

    ssize_t read_line;

    size_t len_line = 0;

    while (1) {

        // 从管道读取进程ID

        if (read(pipefd[0], &info, sizeof(info)) == -1) {

            perror("Error reading from pipe");

            exit(EXIT_FAILURE);

        }

        // 读取进程的完整路径和参数信息

        snprintf(path, sizeof(path), "/proc/%d/cmdline", info.pid);

        file = fopen(path, "r");

        if (file) {

            read_line = getline(&info.cmdline, &len_line, file);

            if (read_line > 0) {

                // 将进程信息写入管道

                if (write(pipefd[1], &info, sizeof(info)) == -1) {

                    perror("Error writing to pipe");

                    exit(EXIT_FAILURE);

                }

            }

            fclose(file);

        }

    }

    return NULL;

}

int main(int argc, char *argv[]) {

    struct sockaddr_nl nlAddr;

    struct nlmsghdr *nlMsgHdr;

    char buf[MAX_PAYLOAD];

    ssize_t len;

    pid_t pid;

    struct process_info info;

    // 创建Netlink socket

    nlSocket = socket(AF_NETLINK, SOCK_RAW, NETLINK_GENERIC);

    if (nlSocket == -1) {

        perror("Error creating Netlink socket");

        exit(EXIT_FAILURE);

    }

    memset(&nlAddr, 0, sizeof(nlAddr));

    nlAddr.nl_family = AF_NETLINK;

    nlAddr.nl_groups = CN_IDX_PROC;

    if (bind(nlSocket, (struct sockaddr *)&nlAddr, sizeof(nlAddr)) == -1) {

        perror("Error binding Netlink socket");

        exit(EXIT_FAILURE);

    }

    // 创建管道

    if (pipe(pipefd) == -1) {

        perror("Error creating pipe");

        exit(EXIT_FAILURE);

    }

    // 创建进程信息获取线程

    pthread_t thread_id;

    if (pthread_create(&thread_id, NULL, get_process_info_thread, NULL) != 0) {

        perror("Error creating thread");

        exit(EXIT_FAILURE);

    }

    while (1) {

                // 接收Netlink消息

        len = recv(nlSocket, buf, MAX_PAYLOAD, 0);

        if (len == -1) {

            perror("Error receiving Netlink message");

            exit(EXIT_FAILURE);

        }

        nlMsgHdr = (struct nlmsghdr *)buf;

        // 遍历所有Netlink消息

        while (NLMSG_OK(nlMsgHdr, len)) {

            // 检查是否是进程事件

            if (nlMsgHdr->nlmsg_type == TASKSTATS_TYPE_PID) {

                struct taskstats *ts = (struct taskstats *)NLMSG_DATA(nlMsgHdr);

                if (ts->ac_exitcode == 0) {

                    pid = ts->ac_pid;

                    // 将进程ID写入管道

                    info.pid = pid;

                    if (write(pipefd[1], &info, sizeof(info)) == -1) {

                        perror("Error writing to pipe");

                        exit(EXIT_FAILURE);

                    }

                }

            }

            // 移动到下一个Netlink消息

            nlMsgHdr = NLMSG_NEXT(nlMsgHdr, len);

        }

        // 从管道读取进程信息并输出

        while (read(pipefd[0], &info, sizeof(info)) > 0) {

            printf("pid=%d, cmdline=%s\n", info.pid, info.cmdline);

        }

    }

    close(nlSocket);

    close(pipefd[0]);

    close(pipefd[1]);

    return 0;

}
