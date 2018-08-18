/// editor_open_code(key);

var key = argument0;

var fpath = objDialogueEditorCodeEditor._codeFilePath;
var notepadPath = objDialogueEditorCodeEditor._notepadPath;

if !objDialogueEditorCodeEditor._unsandboxed {
    show_message('Script file "' + fpath + '"is sandboxed. Please open it up once so we can edit it');
    var result = get_save_filename("gml files|*.gml", fpath);
    if (result == "") {
        return 0;
    }
    objDialogueEditorCodeEditor._unsandboxed = true;
}

var file = file_text_open_read(fpath);

if (file == -1) {
    show_error('FILE READING PROBLEM: File at path "' + fpath + '" Does not exist!', false);
}

// The line where our key is! Notepad++ uses this to open to a particular line number.
var keyLine = -1;

var endLine = -1;

var lineNumber = 0;
var parsing = false;
while (!file_text_eof(file))
{
    var line = file_text_read_string(file);
    lineNumber++;
    
    if string_count("//!START", line) != 0 {
        parsing = true;
    }
    if parsing && string_count("//!END", line) != 0 {
        endLine = lineNumber;
        break;
    }

    if parsing {
        if string_search(line, 0, "case") != -1 {
            var caseEnd = string_search(line, 0, '"');
            var keyEnd = string_search(line, caseEnd+1, '"');
            var caseKey = string_copy(line, caseEnd + 2, keyEnd - caseEnd - 1);
            if key == caseKey {
                // Open Notepad++ at the proper index!
                keyLine = lineNumber;
                break;
            }
        }
    }

    file_text_readln(file);
}
file_text_close(file);

// If we haven't found our key, let's make it!
if (keyLine == -1) {

    var fileWrite = file_text_open_write(fpath);
    file = file_text_open_read(fpath);
    
    // Everything before
    repeat(endLine - 1) {
        var line = file_text_readln(file);
        file_text_write_string(fileWrite, line);
    }
    
    // The extra bit
    file_text_write_string(fileWrite, '    case "' + key + '":');
    file_text_writeln(fileWrite);
    file_text_write_string(fileWrite, '        ');
    file_text_writeln(fileWrite);
    file_text_write_string(fileWrite, '        break;');
    file_text_writeln(fileWrite);

    // Everything after
    while (!file_text_eof(file)) {
        var line = file_text_readln(file);
        file_text_write_string(fileWrite, line);
    }

    file_text_close(file);
    file_text_close(fileWrite);

    keyLine = endLine + 1;
}

// Now open up notepad++:
ExecuteShell('"' + notepadPath + '"' + " -n" + string(keyLine + 1) + " -c9" + ' "' + fpath + '"', true);
