# GRSBPL - Generic Random Stack Based Programming Language

uses some form of reverse polish notation

```
1 5 * 5 +
> 10
```

There is a stack and variables. Operations are done on the stack, and you can store results in variables (a bit like in
the JVM). The stack contains integer values. Floating point numbers are not supported.

When the program finishes (run to the end of the program), the last value on the stack is returned. If the stack is
clear, 0 is always returned. If there is an error during execution, -1 is returned along with an error message to
stderr.

## Operators and keywords:

* any number `n` -> push the numeric value of n
* any character `'c'` -> push c as its escaped ascii value
* `+` -> add two values on the stack, pops both and pushes the result
* `-` -> subtract two values on the stack, pops both and pushes the result
* `*` -> multiply two values on the stack, pops both and pushes the result
* `/` -> divide, pops both and pushes the result
* `%` -> mod, pops both and pushes the result
* `not` -> invert stack value (!=0 -> 0, 0 -> 1)
* `swap` -> swaps the 2 top stack values
* `out` -> pop and output it to the console as ascii
* `nout` -> pop and output as a number to the console
* `in` -> push input char as ascii to the stack
* `# comment #` text between # is ignored
* `# comment\n` text after # is ignored
* `&ident` -> pop and store it in a variable
* `@ident` -> load variable and push it, does not consume the variable
* `:ident` -> define a label
* `goto ident` -> goto a label if the value on the stack is !=0, peek

Identifier: \w

Character escape sequences:
\n, \r, \\, \0, \', \b, \f

## Examples:

FizzBuzz

```grsbpl
1 &i                # init loop counter
:start              # set start label
@i 100 - not goto exit              # if i is 100, exit
@i 15 % not goto print_fizz_buzz        # fizzbuzz
@i 5 % not goto print_buzz              # buzz
@i 3 % not goto print_fizz              # fizz
@i nout '\n' out                         # normal number
:end                # go back here after printing
@i 1 + &i           # increment i
1 goto start        # go back to the start

:print_fizz_buzz
   'F' out 'i' out 'z' out 'z' out 'B' out 'u' out 'z' out 'z' out '\n' out
   goto end
:print_fizz
   'F' out 'i' out 'z' out 'z' out '\n' out
   goto end
:print_buzz
   'B' out 'u' out 'z' out 'z' out '\n' out
   goto end

:exit 0
```

## Some Tips

* Increment a variable:
  `@i 1 + &i`
* Pop a value from the stack and discard it:
  `&dev_null` (just use any unused variable)
* Goto if equal
  `@i 100 - not goto finished`
* Goto not equal
  `@i 100 - goto finished`
* Exit the program
  `... goto exit ... :exit 0`
* Exit with exit code depending on the branch
  ```grsbpl
  ... 
  69 swap goto exit # push 69 to the 2nd stack position
  ... 
  5 swap goto exit # push 5 to the 2nd stack position
  ... 
  :exit &del # pop the top stack value to expose the pushed value
  ```


If you ask yourself "why" the answer is that this was made in an afternoon and I had nothing better to do
