require "logstash/inputs/base"
require "logstash/namespace"
require "socket"
require "netlink"

class LogStash::Inputs::NetlinkProc < LogStash::Inputs::Base
  config_name "netlinkproc"

  # Specify the netlink socket buffer size
  config :buffer_size, :validate => :number, :default => 1024

  def register
    @socket = Netlink::Socket.new(Netlink::NETLINK_NETFILTER)
    @socket.bind(Netlink::NLMSG_NOOP)
  end

  def run(queue)
    buffer = Netlink::Buffer.new(@buffer_size)
    loop do
      @socket.recvmsg(buffer)

      # Parse the netlink message to get the process information
      nlmsg = Netlink::NLMsg.new(buffer.data)
      nlattrs = Netlink::NLAttr.parse(nlmsg.payload)
      pid = nlattrs[0].payload.unpack("L")[0]
      comm = nlattrs[1].payload

      # Construct an event for the new process
      event = LogStash::Event.new(
        "pid" => pid,
        "process_path" => comm,
        "process_params" => ""
      )
      decorate(event)
      queue << event
    end
  end

  def stop
    @socket.close
  end
end
