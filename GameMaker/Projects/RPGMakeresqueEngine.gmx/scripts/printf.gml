/// printf(parse string, ...);
// Python style C printf'ing!

// Should we add the stack trace part to the beginning?
var ADD_STACK_TRACE = true;

if argument_count == 0 {
    show_error("Invalid number of arguments: Must pass at least 1 to printf!", true);
}

var query = argument[0];
var result = query;

// Escape "\%"
// NOTE: WE ARE USING SPECIAL ASCII CHARACTERS HERE THAT MIGHT CAUSE PROBLEMS
// BE CAREFUL WITH THIS
var escapeReplacer = chr(21);
result = string_replace_all(result, "\%", escapeReplacer);

// Replace all un-escaped "%" 's with our variables
for(var i = 0; i < argument_count - 1; i++) {
    result = string_replace(result, "%", string(argument[i + 1]));
}

// Undo the escape we just did
result = string_replace(result, escapeReplacer, "%");

var resultNormal = result;

if ADD_STACK_TRACE {
    var strace = debug_get_callstack();
    var callfrom = strace[1];
    callfrom = string_replace(callfrom, "gml_Object_", "");
    callfrom = string_replace(callfrom, "gml_Script_", "");
    result = "[" + callfrom + "]: " + result;
}

show_debug_message(result);

return resultNormal; // Without stacktrace
