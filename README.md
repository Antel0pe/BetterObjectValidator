# BetterObjectValidator
rough outline

**Problem to Solve**

- One benefit of OOP is easily bundling together related logic/code together in an organized way
- When writing automated tests with complex objects - end up writing lots of assert statements to check specific vals
- Very ugly and annoying
- Solution allows bundling of assertions and checks into an OOP object
- Much more organized, cleaner, readable, and potentially reusable

**Design**
- Create class of object to validate 
- Annotate class with @Validate
- Generates class with all the same fields
  - Possible to make generated class inherit or does each class need its own functionality
- Generated class name: original class name + com.BetterValidator.Validator
- Generated class has builder pattern to assign assertions to each field
  - Can assign multiple assertions to each field
    - on one line
- Contains method that takes in original annotated class called validate()
  - Makes sure each assertion is valid for input 
  - Possible to do opposite? Makes sure each one is false?
- Clone method to duplicate validation objects?