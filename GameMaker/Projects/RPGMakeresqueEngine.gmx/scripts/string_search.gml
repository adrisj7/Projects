/// string_search(string, startIndex, substring);
// Searches for a token within a string, returning the index of the first letter the token was found in.
// If no string was found, returns -1.

var str   = argument0,
    start = argument1,
    token = argument2;

var length = string_length(str);
var tokenLength = string_length(token);

if (tokenLength <= 0) {
    show_error("STRING SEARCH PROBLEM: Token Length can't be zero!", false);
    return -1;
}

// Note, less than or equal to on purpose.
for(var i = start; i <= length - tokenLength; i++) {
    var copied = string_copy(str, i+1, tokenLength);
    if (copied == token) {
        return i;
    }
}

return -1;
