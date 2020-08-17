Change Log
==========

# Next Release
Ensure that access to the constructor is available allowing private constructors to be used
When `enableDefaultPrimitiveValues` is set, always treat primitives as though they have a default value
Only fetch the delegate adapter when the property is going to be serialized/deserialized
Move checks for `isSealed` and `isAbstract` from `create` to `read` since Gson will still serialize the data, even if we cannot deserialize it
Minor performance improvements

# 0.2.0
Allow use to optionally use default primitive values when missing from JSON

# 0.1.0
Initial preview release
