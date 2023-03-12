#!/usr/bin/env python3
import osquery

@osquery.register_plugin
class ListeningPortsPlugin(osquery.TablePlugin):

    def name(self):
        return "listening_ports"

    def columns(self):
        return [
            osquery.TableColumn(name="port", type=osquery.INTEGER),
            osquery.TableColumn(name="protocol", type=osquery.STRING)
        ]

    def generate(self, context):
        columns = self.columns()
        query_data = []

        # run `netstat -tln` command and parse output to get listening ports
        netstat_output = os.popen("netstat -tln").read().strip()
        for line in netstat_output.split("\n")[2:]:
            fields = line.split()
            if len(fields) >= 4:
                proto = fields[0]
                if proto.startswith('tcp'):
                    port = int(fields[3].split(':')[-1])
                    query_data.append({columns[0].name: port, columns[1].name: 'tcp'})
                elif proto.startswith('udp'):
                    port = int(fields[3].split(':')[-1])
                    query_data.append({columns[0].name: port, columns[1].name: 'udp'})

        return query_data
