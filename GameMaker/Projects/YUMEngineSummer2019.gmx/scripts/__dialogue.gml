#define __dialogue
/// __dialogue()

    // Dialogue! Here's the big one
    enum DialogueType {
        Text,
        Choice,
        Script,
        Save
    }

    enum DialogueText {
        text,
        sizeof
    }

    enum DialogueChoice {
        choices,
        scripts,
        default_choice,
        sizeof
    }

    enum DialogueScript {
        script,
        args,
        sizeof
    }


#define __get_dialogue_system
/// __get_dialogue_system()
    // returns the dialogue system singleton
    return singleton_get(__YUMEngineDialogueSystem);


#define __get_dialogue_queue_stack
/// __get_dialogue_queue_stack()
    // returns the stack of dialogue queues

    var s = __get_dialogue_system();
    return s._dialogue_queue_stack;


#define __get_dialogue_current_queue
/// __get_dialogue_current_queue()
    // returns the current queue that holds our dialogues

    var qs = __get_dialogue_queue_stack();
    if ds_list_empty(qs) {
        // Start with one
        __dialogue_queue_stack_push();
    }
    return ds_list_stack_top(qs);


#define __dialogue_debug_print_queue_stack
/// __dialogue_debug_print_queue_stack()
    // Prints the current dialogue queue
    show_debug_message("[DEBUG] Dialogue STATUS PRINT START!");
    var qs = __get_dialogue_queue_stack();
    if ds_list_empty(qs) {
        show_debug_message("    Empty");
    }
    // Grab each queue from the stack (top first)
    for (var i = ds_list_size(qs) - 1; i >= 0; --i) {
        // This is the current queue. Queues start at the bottom
        var q = qs[| i];
        show_debug_message("    QUEUE HEIGHT: " + string(i));
        for (var j = 0; j < ds_list_size(q); ++j) {
            // This is the current dialogue element pair
            var elem = q[| j];
            var type = pair_get_first(elem);
            var data = pair_get_second(elem);
            switch type {
                case DialogueType.Text:
                    var t/*:DialogueText*/ = data;
                    show_debug_message("        TEXT: " + t[DialogueText.text]);
                    break;
                case DialogueType.Choice:
                    var c/*:DialogueChoice*/ = data;
                    var line = "        CHOICE: ";
                    var choice_array = c[DialogueChoice.choices];
                    for(var option = 0; option < array_length_1d(choice_array); ++option) {
                        line += choice_array[option];
                        if option != array_length_1d(choice_array) - 1 {
                            line += ", ";
                        }
                    }
                    for(var option = 0; option < array_length_1d(choice_array); ++option) {
                        var scripts = c[DialogueChoice.scripts];
                        var scr = scripts[option];
                        show_debug_message("                " + choice_array[option] + " -> " + script_get_name(scr));
                    }
                    show_debug_message(line);
                    break;
                case DialogueType.Script:
                    var s/*:DialogueScript*/ = data;
                    show_debug_message("        SCRIPT: " + script_get_name(s[DialogueScript.script]));
                    break;
            }
        }
    }
    show_debug_message("[DEBUG] Dialogue STATUS PRINT STOP!");


#define __dialogue_queue_stack_push
/// __dialogue_queue_stack_push()
    var new_queue = ds_list_create();
    var qs = __get_dialogue_queue_stack();
    ds_list_stack_push(qs, new_queue);


#define __dialogue_queue_stack_pop
/// __dialogue_queue_stack_pop()
    // Pop off the top queue, and destroy it as well (cleanup!)

    var qs = __get_dialogue_queue_stack();

    // We must have at least one
    if ds_list_size(qs) == 1 {
        return 0;
    }

    // Cleanup
    var top = __get_dialogue_current_queue();
    ds_list_destroy(top);

    // Poppin
    ds_list_stack_pop(qs);


#define __dialogue_current_queue_add
/// __dialogue_current_queue_add(type, data_struct)
    var type = argument0, data_struct = argument1;

    // Create the dialogue type/data pair
    var q = __get_dialogue_current_queue();
    var pairing = pair_create();
    pair_set_first(pairing, type);
    pair_set_second(pairing, data_struct);

    // Add that pair to the dialogue queue
    ds_list_queue_push(q, pairing);

    // If we don't have an active dialogue, start it!
    if !dialogue_is_open() {
        __dialogue_next();
    }


#define __dialogue_queue_get_front_type
/// __dialogue_queue_get_front_type()
    // Gets the earliest added dialogue's type, or the first in queue
    var q = __get_dialogue_current_queue();
    var p = ds_list_queue_front(q);
    return pair_get_first(p);


#define __dialogue_queue_get_back_type
/// __dialogue_queue_get_back_type()
    // Gets the most recently added dialogue's type
    var q = __get_dialogue_current_queue();
    var p = ds_list_stack_top(q);
    return pair_get_first(p);

#define __dialogue_queue_get_front_data
/// __dialogue_queue_get_front_data()
    var q = __get_dialogue_current_queue();
    var p = ds_list_queue_front(q);
    return pair_get_second(p);


#define __dialogue_queue_get_back_data
/// __dialogue_queue_get_back_data()
    var q = __get_dialogue_current_queue();
    var p = ds_list_stack_top(q);
    return pair_get_second(p);


#define __dialogue_queue_get_up_next
/// __dialogue_queue_get_up_next()
    // Gets the dialogue that would come after the current one
    var s = __get_dialogue_queue_stack();
    // Get next queue in line
    if ds_list_empty(s) {
        // If we got nothing, we're done.
        return noone;
    }

    var q = __get_dialogue_current_queue();

    // It's OK to pop EMPTY queues off here because we have no use for them ever
    while ds_list_size(s) != 1 && ds_list_empty(q) {
        ds_list_stack_pop(s);
        q = ds_list_stack_top(s);
    }
    // If we still got nothing, we're done.
    if ds_list_empty(q) {
        return noone;
    }

    return ds_list_queue_front(q);


#define __dialogue_queue_get_up_next_type
/// __dialogue_queue_get_up_next_type()
    var n = __dialogue_queue_get_up_next();
    if (n == noone) return noone;
    return pair_get_first(n);


#define __dialogue_queue_get_up_next_data
/// __dialogue_queue_get_up_next_data()
    var n = __dialogue_queue_get_up_next();
    if (n == noone) return noone;
    return pair_get_second(n);


#define __dialogue_pop
/// __dialogue_pop()
    // Removes the current dialogue element
    var s = __get_dialogue_system();
    var q = __get_dialogue_current_queue();

    // If we are empty, keep poppin!
    var qs = __get_dialogue_queue_stack();
    while ds_list_empty(q) && ds_list_size(qs) != 1 {
        __dialogue_queue_stack_pop();
        q = __get_dialogue_current_queue();
    }

    ds_list_queue_pop(q);


#define __dialogue_next
/// __dialogue_next()
    // Gets and opens the next dialogue

    // Uncomment this to check the dialogue queue stack every time we move on
    // show_debug_message("[DEBUG] DIALOGUE NEXT BEFORE: ");
    // __dialogue_debug_print_queue_stack();

    var s = __get_dialogue_system();
    var q = __get_dialogue_current_queue();

    // If we are empty, keep poppin!
    var qs = __get_dialogue_queue_stack();
    while ds_list_empty(q) && ds_list_size(qs) != 1 {
        __dialogue_queue_stack_pop();
        q = __get_dialogue_current_queue();
    }

    // If there are STILL no more dialogues, we're done
    if ds_list_empty(q) {
        s._dialogue_open = false;
        return 0;
    }

    // Otherwise, start the next one!

    // We're dialoguing
    s._dialogue_open = true;

    // Handle queue
    var type = __dialogue_queue_get_front_type();
    var data = __dialogue_queue_get_front_data();
    //ds_list_queue_pop(q);
    __dialogue_pop();


    // This is kinda like a dialogue "Create Event"
    switch type {
        case DialogueType.Text:
            __dialogue_start_text(data);
            break;
        case DialogueType.Choice:
            __dialogue_start_choice(data, true);
            break;
        case DialogueType.Script:
            __dialogue_start_script(data);
            break;
        default:
            show_error("Invalid Dialogue Type given: " + string(type) + ". Grabbing next one", false);
            return __dialogue_next();
    }


#define __dialogue_start_text
/// __dialogue_start_text(data)
    // Starts a text dialogue, no questions asked
    var data/*:DialogueText*/ = argument0;

    var sys = __get_dialogue_system();
    var r = sys._text_renderer;

    r._text = data[DialogueText.text];

    r._active = true;
    r._text_active = false;
    r._waiting = false;
    r._waiting_closing = false;
    r._waiting_next = false;
    r._closed_by_choice = false; // Not used (yet)
    // Defaults
    r._text_speed = 1;

    r._char_index = 0;
    dialogue_opener_open(r._opener);


#define __dialogue_start_choice
/// __dialogue_start_choice(data, auto_scale)
    var data/*:DialogueChoice*/ = argument0;
    var auto_scale = argument1;

    var sys = __get_dialogue_system();
    var r = sys._choice_renderer;

    var num_choices = array_length_1d(data[DialogueChoice.choices]);

    // Copy over choices and init script array
    r._choices = data[DialogueChoice.choices];
    r._scripts = data[DialogueChoice.scripts];

    // Debug print scripts
    // show_debug_message("[DEBUG] START CHOICES: ");
    // var c = r._choices,
    //     s = r._scripts;
    // for(var i = 0; i < num_choices; ++i) {
    //     var name = "<undefined>";
    //     var script = s[@ i];
    //     if script_exists(script)
    //         name = script_get_name(script);
    //     show_debug_message(c[@ i] + " -> " + name);
    // }

    // Init other stuff
    r._active = true;
    r._choice_index = 0;
    r._default_choice = noone;

    // Width and height of choice window
    var max_width = 0;
    for(var i = 0; i < num_choices; ++i) {
        var cs = data[DialogueChoice.choices];
        var str = cs[@ i];
        draw_set_font(dialogue_get_default_font());
        var w = dialogue_text_width(str);
        if w > max_width
            max_width = w;
        // Also handle default choice
        if data[DialogueChoice.default_choice] == str {
            r._default_choice = i;
        }
    }
    if auto_scale {
        var width = max_width + 2 * dialogue_get_text_padding_x();
        var height = num_choices * (
                string_height("A") + dialogue_get_text_spacing()
            ) + 2 * dialogue_get_text_padding_y();
        // Create that choice window
        var o = r._opener;
        dialogue_opener_set_window_size(o, width, height);
        dialogue_opener_set_window_position(o, 
                        view_wview[0]/2 - width/2, view_hview[0]/2 - height/2
        );
    }
    dialogue_opener_open(r._opener);


#define __dialogue_start_script
/// __dialogue_start_script(data)
    var data/*:DialogueScript*/ = argument0;

    script_execute_args_array(data[DialogueScript.script], data[DialogueScript.args]);
    __dialogue_next();


#define __dialogue_stop_text
/// __dialogue_stop_text()
    // closes the text renderer
    var sys = __get_dialogue_system();
    var r = sys._text_renderer;
    with r {
        dialogue_opener_close(_opener);
    }


#define __dialogue_text_step
/// __dialogue_text_step(index)
    var index = argument0;
    // Parses a command starting at an index where a backslash is
    // returns how many characters we've moved

    var length = string_length(_text);
    var char_at = string_char_at(_text, index);
    if char_at == '\' {
        // Special cases!
        if (index + 1 >= length) {
            show_error("Invalid escape character at end of string: '" + _text +  "'!", false);
            return 1;
        }
        char_at = string_char_at(_text, index + 1);
        switch char_at {
            // escape backslash
            case '\':
                return 2;
            // 1 second delay
            case '|':
                _wait_buffer = 60;
                return 2;
            // 1/4th second delay
            case '.':
                _wait_buffer = 15;
                return 2;
            // End message instantly
            case '^':
                _text_active = false;
                __dialogue_stop_text();
                return 0;
            // Instantly Draw text btwn \> and \<
            case '>':
                var prev = index;
                // Move index to the end of the text
                while !(
                    string_char_at(_text, index    ) == '\' &&
                    string_char_at(_text, index + 1) == '<'
                    ) {
                        ++index;
                        if index >= length {
                            index = length - 1;
                            break;
                        }
                }
                return index - prev + 2; // + 2 to go past the \<
            // Wait for user input to continue
            case '!':
                // TODO: Implement
                _waiting_next = true;
                return 2;
            case 'S':
            case 'V':
            case 'C':
                // TODO: Modularize this
                // Skip C[
                // index is now after C[
                index += 3;
                // one digit or two digit supported
                var offs = 1;
                if string_char_at(_text, index + 2) == ']'
                    offs = 2;
                if string_char_at(_text, index+offs) != ']' {
                    show_error("Invalid bracketing: " + string_copy(_text, index - 2, offs + 5), false);
                    return offs + 3; // 3 to go past \C[
                }
                var code = real(string_copy(_text, index, offs));
                if char_at == 'S' {
                    // Set speed
                    _text_speed = code;
                }
                return offs + 3;

        }
    } else {
        // We have a regular character, so it's all good.
        return 1;
    }


#define __dialogue_check_progress
/// __dialogue_check_progress()

    var sys = __get_dialogue_system();

    return sys._input_progress;


#define __dialogue_check_progress_pressed
/// __dialogue_check_progress_pressed()

    var sys = __get_dialogue_system();

    return sys._input_progress && !sys._input_progress_last;


#define __dialogue_check_cancel
/// __dialogue_check_cancel()

    var sys = __get_dialogue_system();
    return sys._input_cancel;


#define __dialogue_check_cancel_pressed
/// __dialogue_check_cancel_pressed()

    var sys = __get_dialogue_system();

    return sys._input_cancel && !sys._input_cancel_last;


#define __dialogue_check_up
/// __dialogue_check_up()

    var sys = __get_dialogue_system();
    return sys._input_up;
#define __dialogue_check_down
/// __dialogue_check_down()

    var sys = __get_dialogue_system();
    return sys._input_down;


#define __dialogue_get_scale_factor
/// __dialogue_get_scale_factor()
    // Dialogue factors are scaled by the default 192x192 sprite
    // so if our sprite window size changes, add some scaling
    var spr = dialogue_get_window_sprite();
    return sprite_get_width(spr) / 192;


#define dialogue
/// dialogue(ftext)
    // Adds a text dialogue to our queue
    var ftext = argument0;
    var type = DialogueType.Text;

    var t/*:DialogueText*/ = array_create(DialogueText.sizeof);
    t[@DialogueText.text] = ftext;

    __dialogue_current_queue_add(type, t);


#define dialogue_choice
/// dialogue_choice(choices...)

    // If we got no choices, don't make a window.
    if argument_count == 0 {
        return -1;
    }

    // Copy over our args
    var choices = array_create(argument_count);
    for(var i = 0; i < argument_count; ++i) {
        choices[@ i] = argument[i];
    }

    var type = DialogueType.Choice;
    var c/*:DialogueChoice*/ = array_create(DialogueChoice.sizeof);
    c[@DialogueChoice.choices] = choices;
    c[@DialogueChoice.scripts] = array_create(array_length_1d(c[DialogueChoice.choices]));
    c[@DialogueChoice.default_choice] = "";
    var scr = c[DialogueChoice.scripts];
    // Init all scripts to a negative value so no scripts are called
    for(var i = 0; i < array_length_1d(scr); ++i) {
        scr[@ i] = noone;
    }

    __dialogue_current_queue_add(type, c);


#define dialogue_choose
/// dialogue_choose(choice, script)
    var choice = argument0, script = argument1;
    // Adds scripts to the dialogue

    if __dialogue_queue_get_back_type() != DialogueType.Choice {
        // Don't mess with the dialogue if we're calling it at the wrong time
        show_error("You tried setting a choice where there wasn't any!", false);
        return false;
    }

    // Find the corresponding choice
    var dat/*:DialogueChoice*/ = __dialogue_queue_get_back_data();
    var choices = dat[DialogueChoice.choices];
    var scripts = dat[DialogueChoice.scripts];

    for (var i = 0; i < array_length_1d(choices); ++i) {
        if choices[@ i] == choice {
            show_debug_message("[DEBUG] INTERNAL: " + choice + " -> " + script_get_name(script));
            scripts[@ i] = script;
            return true;
        }
    }

    show_debug_message("BEFORE THE ERROR, Here's some useful info: ");
    for (var i = 0; i < array_length_1d(choices); ++i) {
        show_debug_message("CHOICE #" + string(i) + ": " + choices[i]);
    }
    show_error('Choice "' + choice + '" not found!', false);
    return false;


#define dialogue_choice_set_default
/// dialogue_choice_set_default(choice)
    var choice = argument0;

    if __dialogue_queue_get_back_type() != DialogueType.Choice {
        // Don't mess with the dialogue if we're calling it at the wrong time
        show_error("You tried setting a choice where there wasn't any!", false);
        return false;
    }

    // Find the corresponding choice
    var dat/*:DialogueChoice*/ = __dialogue_queue_get_back_data();

    dat[@DialogueChoice.default_choice] = choice;


#define dialogue_script
/// dialogue_script(script, ...)
    var script = argument[0];
    var type = DialogueType.Script;

    var s/*:DialogueScript*/ = array_create(DialogueScript.sizeof);
    s[@DialogueScript.script] = script;
    var args = array_create(argument_count - 1);
    for(var i = 1; i < argument_count; ++i)
        args[i - 1] = argument[i];
    s[@DialogueScript.args] = args;

    __dialogue_current_queue_add(type, s);


#define dialogue_progress
/// dialogue_progress(progress_input)
    // Tells the dialogue system to progress (press Z basically)
    var progress_input = argument0;

    var sys = __get_dialogue_system();

    sys._input_progress = progress_input;


#define dialogue_cancel
/// dialogue_cancel(cancel_input)
    // Tells the dialogue system to cancel (press X basically)
    var cancel_input = argument0;

    var sys = __get_dialogue_system();

    sys._input_cancel = cancel_input;


#define dialogue_up
/// dialogue_up(up_input)
    var up_input = argument0;

    var sys = __get_dialogue_system();
    sys._input_up = up_input;


#define dialogue_down
/// dialogue_down(down_input)
    var down_input = argument0;

    var sys = __get_dialogue_system();
    sys._input_down = down_input;


#define dialogue_set_window_sprite
/// dialogue_set_window_sprite(sprite)
    // sets the dialogue system window sprite
    var sprite = argument0;

    var sys = __get_dialogue_system();
    sys._window_sprite = sprite;

    // Extract colors
    var w = sprite_get_width(sprite),
        h = sprite_get_height(sprite);
    var temp_surf = surface_create(w/2, h/4);
    surface_set_target(temp_surf);
    draw_sprite_part(sprite, 0, w/2, h*3/4, w/2, h/4, 0, 0);
    // Extract 32 colors, each color a 16th of the window sprite.
    var col_w = w / 16,
        col_h = h / 16;
    for(var i = 0; i < 32; ++i) {
        var xx = col_w * (i % 8),
            yy = col_h * floor(i / 8);
        var c = surface_getpixel(temp_surf, xx, yy);
        sys._text_colors[@ i] = c;
    }
    surface_reset_target();


#define dialogue_set_default_font
/// dialogue_set_default_font(font)
    var font = argument0;
    var sys = __get_dialogue_system();
    sys._default_font = font;


#define dialogue_enable_auto_menu
/// dialogue_enable_auto_menu(enable)
    var enable = argument0;
    var sys = __get_dialogue_system();
    sys._auto_menu = enable;


#define dialogue_text_width
/// dialogue_text_width(text)
    var text = argument0;
    // Gets width of text, without any wrapping

    return dialogue_text_width_ext(text, 0, -1);


#define dialogue_text_width_ext
/// dialogue_text_width(text, sep, w)
    var text = argument0, sep = argument1, w = argument2;
    // Gets the width of the text, not including special codes

    var current_w = 0;
    draw_set_font(dialogue_get_default_font());
    for (var i = 1; i <= string_length(text); ++i) {
        var draw = true;
        var char = string_char_at(text, i);
        // Parse commands
        if char == '\' {
            var next = string_char_at(text, i+1);
            switch next {
                case 'C':
                case 'S':
                case 'V':
                    draw = false;
                    i += 3;
                    // one digit or two digit supported
                    var offs = 1;
                    if string_char_at(text, i + 2) == ']'
                        offs = 2;
                    if string_char_at(text, i + offs) != ']' {
                        show_error("Invalid bracketing: " + string_copy(text, i - 2, offs + 5), false);
                    }                    // 1 to skip 
                    i += offs;
                    break;
                case '\':
                    draw = true;
                    i += 1;
                    break;
                default: // Assume 1 more
                    draw = false;
                    i += 1;
                    break;
            }
        }
        if draw {
            current_w += string_width(char);
            // Handle w and sep
            if current_w > w  && w != -1 {
                current_w = 0;
            }
        }
    }
    return current_w;


#define dialogue_draw_text
/// dialogue_draw_text(xpos, ypos, text)
    var xpos = argument0, ypos = argument1, text = argument2;
    // Draws dialogue text without any extra arguments
    dialogue_draw_text_ext(xpos, ypos, text, string_length(text), 0, -1);


#define dialogue_draw_text_ext
/// __dialogue_draw_text_ext(xpos, ypos, text, stringlength, sep, w)
    var xpos = argument0, ypos = argument1, text = argument2, stringlength = argument3, sep = argument4, w = argument5;
    // Draws text, formatted. If a command is started but cropped,
    // it is included
    var current_w = 0;
    var current_h = 0;
    draw_set_font(dialogue_get_default_font());
    draw_set_color(dialogue_get_color(0));
    draw_set_valign(fa_top);
    draw_set_halign(fa_left);

    for (var i = 1; i <= stringlength; ++i) {
        var draw = true;
        var char = string_char_at(text, i);
        // Parse commands
        if char == '\' {
            var next = string_char_at(text, i+1);
            var color = false;
            switch next {
                case 'C':
                    color = true;
                case 'S':
                case 'V':
                    draw = false;
                    // Skip C[
                    // index is now after C[
                    // ex.
                    // \C[1]
                    i += 3;
                    // one digit or two digit supported
                    var offs = 1;
                    if string_char_at(text, i + 2) == ']'
                        offs = 2;
                    if string_char_at(text, i + offs) != ']' {
                        show_error("Invalid bracketing: " + string_copy(text, i - 2, offs + 5), false);
                    } else if color {
                        var col_code = real(string_copy(text, i, offs));
                        draw_set_color(dialogue_get_color(col_code));
                    }
                    // 1 to skip 
                    i += offs;
                    break;
                case '\':
                    draw = true;
                    i += 1;
                    break;
                default: // Assume 1 more
                    draw = false;
                    i += 1;
                    break;
            }
        }
        if draw {
            draw_text(xpos + current_w, ypos + current_h, char);
            current_w += string_width(char);
            // Handle w and sep
            if current_w > w && w != -1 {
                current_w = 0;
                current_h += sep;
            }
        }
    }


#define dialogue_draw_window
/// dialogue_draw_window(xpos, ypos, width, height)
    var xpos = argument0, ypos = argument1, width = argument2, height = argument3;

    var sys = __get_dialogue_system();
    var s = sys._window_sprite;
    var w = sprite_get_width(s),
        h = sprite_get_height(s);

    var color = c_white,
        alpha = draw_get_alpha();

    // MAIN DRAW ORDER:
    /* 
     * 1) top left BODY background stretched from top left corner to bottom 
     * right corner (- 4 pixels on both corners)
     * 2) bottom left Pattern background tiled from same corners
     * 3) the border
     */

    // Stuff inside the window (bumped up)
    var bump = 4 * __dialogue_get_scale_factor(); // TODO: Should this be setabble?
    var left    = xpos + bump,
        right   = xpos + width - bump,
        top     = ypos + bump,
        bot     = ypos + height - bump,
        inner_w = right - left,
        inner_h = bot - top;

    // Body background stretched
    draw_sprite_part_ext(s, 0,
                         0, 0,
                         0.5*w, 0.5*h,
                         left, top,
                         inner_w / (0.5*w), inner_h / (0.5*h),
                         color, alpha
    );
    // Pattern tiled
    draw_sprite_part_tiled_size(s, 0,
                                0, 0.5*h,
                                0.5*w, 0.5*h,
                                left, top,
                                inner_w, inner_h
    );
    // Now the big wabagalooza
    draw_sprite_part_corners(s, 0,
                             0.5*w, 0,
                             0.5*w, 0.5*h,
                             18 * __dialogue_get_scale_factor(),// TODO: Should this be setabble?
                             xpos, ypos,
                             width, height);
    // No way... And we're done!


#define dialogue_draw_cursor
/// dialogue_draw_cursor(xpos, ypos, width, height, fill_inside)
    var xpos = argument0, ypos = argument1, width = argument2, height = argument3, fill_inside = argument4;

    var sys = __get_dialogue_system();
    var s = sys._window_sprite;
    var w = sprite_get_width(s),
        h = sprite_get_height(s);

    var color = c_white,
        alpha = draw_get_alpha();

    var bump = 14 * __dialogue_get_scale_factor();
    // Border
    draw_sprite_part_corners(s, 0,
                             0.5 * w,
                             0.5 * h,
                             0.25 * w, 0.25 * h,
                             bump,
                             xpos, ypos,
                             width, height
    );
    if fill_inside {
        // Inner thing
        var draw_width  = width      - (2 * bump),
            draw_height = height     - (2 * bump),
            part_width  = (0.25 * w) - (2 * bump),
            part_height = (0.25 * h) - (2 * bump);
        draw_sprite_part_ext(s, 0,
                              (0.5 * w) + bump, (0.5 * h) + bump,
                              part_width, part_height,
                              xpos + bump, ypos + bump,
                              draw_width / part_width, draw_height / part_height,
                              color, alpha
        );
    }

#define dialogue_draw_choice
/// dialogue_draw_choice(xpos, ypos, width, height, choices, selected, time)
    var xpos = argument0, ypos = argument1, width = argument2, height = argument3, choices = argument4, selected = argument5, time = argument6;
    draw_set_font(dialogue_get_default_font());

    var tpx = dialogue_get_text_padding_x(),
        tpy = dialogue_get_text_padding_y();

    var bump = 4 * __dialogue_get_scale_factor();

    var tx = xpos + tpx,
        ty = ypos + tpy;
    var text_height = string_height("A");
    var dh = text_height + dialogue_get_text_spacing();
    for(var i = 0; i < array_length_1d(choices); ++i) {

        var text = choices[i];
        //var twidth = dialogue_text_width(text);
        dialogue_draw_text(tx, ty, text);
        // draw_set_halign(fa_center);
        // draw_text(tx, ty, _choices[i]);
        // draw_set_halign(fa_left);

        // Draw cursor over our selected
        if i == selected {
            //draw_set_alpha( osc(0.1, 1, 60 ,_select_timer) );
            var fill_inside = (time % 20) > 10;
            dialogue_draw_cursor(tx - bump, ty - bump,
                                 width - 2*tpx + 2*bump, text_height + 2*bump, fill_inside);
            //draw_set_alpha(1);
        }

        ty += dh;
    }


#define dialogue_draw_bouncer
/// dialogue_draw_bouncer(xpos, ypos, index)
    // Draw the little bouncy thing that appears when a dialogue is done
    var xpos = argument0, ypos = argument1, index = argument2;

    var sys = __get_dialogue_system();
    var s = sys._window_sprite;
    var w = sprite_get_width(s),
        h = sprite_get_height(s);

    index = floor(index) % 4;

    var bounce_w = w / 8,
        bounce_h = h / 8;

    var left = w * 3 / 4;
    if index % 2 == 1 {
        left += bounce_w;
    }
    var top = w/2;
    if index >= 2 {
        top += bounce_h;
    }

    draw_sprite_part(s, 0,
                     left, top,
                    bounce_w, bounce_h,
                    xpos - bounce_w/2, ypos - bounce_h/2
    );

#define dialogue_opener_create
/// dialogue_opener_create(xpos, ypos, width, height)
    var xpos = argument0, ypos = argument1, width = argument2, height = argument3;
    // Creates a dialogue window opener
    var res = instance_create(0, 0, __YUMEngineDialogueWindowOpener);
    res._window_x = xpos;
    res._window_y = ypos;
    res._window_width = width;
    res._window_height = height;

    return res;


#define dialogue_opener_delete
/// dialogue_opener_delete(opener)
    var opener = argument0;
    instance_destroy(opener);


#define dialogue_opener_open
/// dialogue_opener_open(opener)
    var opener = argument0;
    // Opens a dialogue opener
//    opener._progress = 0;
    opener._opening = true;
    if opener._open_duration == 0 {
        opener._progress = 1;
    }


#define dialogue_opener_close
/// dialogue_opener_close(opener)
    var opener = argument0;
    // Closes a dialogue opener
    opener._opening = false;
    if opener._close_duration == 0 {
        opener._progress = 0;
    }

#define dialogue_opener_set_window_position
/// dialogue_opener_set_window_position(opener, xpos, ypos)
    var opener = argument0, xpos = argument1, ypos = argument2;
    opener._window_x = xpos;
    opener._window_y = ypos;


#define dialogue_opener_set_window_size
/// dialogue_opener_set_window_size(opener, width, height)
    var opener = argument0, width = argument1, height = argument2;
    opener._window_width = width;
    opener._window_height = height;


#define dialogue_opener_set_open_duration
/// dialogue_opener_set_open_duration(opener, frames)
    var opener = argument0, frames = argument1;
    opener._open_duration = frames;


#define dialogue_opener_set_close_duration
/// dialogue_opener_set_close_duration(opener, frames)
    var opener = argument0, frames = argument1;
    opener._close_duration = frames;


#define dialogue_opener_is_open
/// dialogue_opener_is_open(opener)
    // Are we completely open?
    var opener = argument0;
    return dialogue_opener_get_progress(opener) == 1 && opener._opening;


#define dialogue_opener_is_closed
/// dialogue_opener_is_closed(opener)
    // Are we completely closed?
    var opener = argument0;
    return dialogue_opener_get_progress(opener) == 0 && !opener._opening;


#define dialogue_opener_get_progress
/// dialogue_opener_get_progress(opener)
    var opener = argument0;
    return opener._progress;


#define dialogue_opener_get_window_x
/// dialogue_opener_get_window_x(opener)
    var opener = argument0;
    return opener._window_x;
#define dialogue_opener_get_window_y
/// dialogue_opener_get_window_y(opener)
    var opener = argument0;
    return opener._window_y;
#define dialogue_opener_get_window_width
/// dialogue_opener_get_window_width(opener)
    var opener = argument0;
    return opener._window_width;
#define dialogue_opener_get_window_height
/// dialogue_opener_get_window_height(opener)
    var opener = argument0;
    return opener._window_height;


#define dialogue_is_open
/// dialogue_is_open()
    // Are there any dialogues currently running?
    var s = __get_dialogue_system();
    return s._dialogue_open;



#define dialogue_get_window_sprite
/// dialogue_get_window_sprite()
    var sys = __get_dialogue_system();
    return sys._window_sprite;


#define dialogue_get_color
/// dialogue_get_color(color_index)
    var color_index = argument0;
    // Uses the window sprite to grab a color

    var sys = __get_dialogue_system();
    return sys._text_colors[@ color_index];


#define dialogue_get_default_font
/// dialogue_get_default_font()
    var sys = __get_dialogue_system();
    return sys._default_font;


#define dialogue_get_text_padding_x
/// dialogue_get_text_padding_x()
    // Gets the x padding of text from the left of a window
    // TODO: Store this as a variable somewhere
    return 6;//10;
#define dialogue_get_text_padding_y
/// dialogue_get_text_padding_y()
    // Gets the y padding of text from the top of a window
    // TODO: Store this as a variable somewhere
    return 10;//6;
#define dialogue_get_text_spacing
/// dialogue_get_text_spacing()
    // Gets the spacing of text between each line
    // TODO: Store this as a variable somewhere
    return 8     * __dialogue_get_scale_factor();