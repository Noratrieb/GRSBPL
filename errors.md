# All GRSBPL Error Messages

## Syntax Errors

- Any uncaught exception occurs during lexing:  
  `Unknown Syntax Error. <exceptionname>: <exceptionmessage>`

- Invalid character escaped:  
  `Invalid escape sequence <escaped>`

- Integer parse failed (can only happen because number is too big)  
  `Value not an integer: <number>`

## Runtime Errors

- Label not found  
  `Label '<name>' not found`

- Function not found  
  `Function '<name>' not found`

- Stack empty on return  
  `Function has to return some value, but no value was found on the stack`

- Pop called on empty stack  
  `Cannot pop empty stack`

- No stack frame left after return  
  `Tried to return outside of function, probably forgot to skip a function`

- Stackoverflow - limit 1 000 000  
  `Stackoverflow. Limit of <STACK_LIMIT> stack frames reached.`

- Invalid token found  
  `Excepted token '<name>' but found '<name>'`

- Failed to read input from stdin  
  `[VM] - Error reading input`