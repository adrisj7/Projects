/// file_text_read_string_full(file);
// Reads the entirity of a file, returning the string contents of a file.

var file = argument0;

var result = "";

while (!file_text_eof(file))
{
    result += file_text_read_string(file);
    file_text_readln(file);
}

return result;
