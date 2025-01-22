package org.xplorg.tool.telco360.performanceDAO.implementation;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.util.ArrayList;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;

public class command_task_version3 implements Runnable {
    Session           session;
    ArrayList<String> commands;
    ArrayList<String>     sb;

    public command_task_version3(Session session, ArrayList<String> commands, ArrayList<String> sb) {
        super();
        this.session  = session;
        this.commands = commands;
        this.sb       = sb;
    }

    @Override
    public void run() {
        ArrayList<String> arlst_output = new ArrayList<String>();

        try {
            PipedOutputStream commandIO    = new PipedOutputStream();
            InputStream       sessionInput = new PipedInputStream(commandIO);
            ChannelShell      channel      = (ChannelShell) session.openChannel("shell");

            channel.setInputStream(sessionInput);

            InputStream sessionOutput = channel.getInputStream();

            channel.connect();
            Thread.sleep(1000);

            if (channel.isConnected()) {
                String command;

//              Read input until we get the 'Password:' prompt
                for (int i = 0; i < commands.size(); i++) {
                    command = commands.get(i) + "\n";

                //     System.out.println(command);
                    commandIO.write(command.getBytes());
                    commandIO.flush();

                     Thread.sleep(1000);
                }
            }

            int    i   = 0;
            byte[] tmp = new byte[4096];

            while ((i = sessionOutput.read(tmp, 0, tmp.length)) != -1) {
                String str = new String(tmp, 0, i);

                arlst_output.add(str);

                if (str.contains("quit") || str.contains("quittt")) {
                  

                    break;
                }
            }

            for (int j = 0; j < arlst_output.size(); j++) {
                sb.add(arlst_output.get(j).replace(" [1D", ""));
            }
            session.disconnect();
        } catch (Exception e) {}
    }
}


