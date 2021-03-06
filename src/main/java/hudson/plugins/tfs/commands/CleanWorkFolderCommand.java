package hudson.plugins.tfs.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.plugins.tfs.util.MaskedArgumentListBuilder;

public class CleanWorkFolderCommand extends AbstractCommand implements ParseableCommand<List<String>> {
    private static final Pattern ITEM_PATTERN = Pattern.compile("\\w+\\s+(.*)");
    private static final Pattern PATH_PATTERN = Pattern.compile("(.+):");
    
    private final String workFolder;

    public CleanWorkFolderCommand(ServerConfigurationProvider configurationProvider, String workFolder) {
        super(configurationProvider);
        this.workFolder = workFolder;
    }

    public MaskedArgumentListBuilder getArguments() {
        MaskedArgumentListBuilder arguments = new MaskedArgumentListBuilder();        
        arguments.add("undo");
        arguments.add(workFolder);
        arguments.add("-recursive");
        arguments.add("-noprompt");
        addLoginArgument(arguments);
        return arguments;
    }

    public List<String> parse(Reader r) throws ParseException, IOException {
        BufferedReader reader = new BufferedReader(r);
        List<String> list = new ArrayList<String>();
        
        String line = reader.readLine();
        String lastPath = null;
        while (line != null) {
            Matcher matcher = PATH_PATTERN.matcher(line);
            if (matcher.matches()) {
                lastPath = matcher.group(1);
            } else {                
                if (lastPath != null) {
                    matcher = ITEM_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        list.add(lastPath + "\\" + matcher.group(1));
                    }
                }
            }            
            line = reader.readLine();
        }
        return list;
    }
}
