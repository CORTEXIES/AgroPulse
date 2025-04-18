file(REMOVE_RECURSE
  "../../bin/libmtmd_shared.dylib"
  "../../bin/libmtmd_shared.pdb"
)

# Per-language clean rules from dependency scanning.
foreach(lang CXX)
  include(CMakeFiles/mtmd_shared.dir/cmake_clean_${lang}.cmake OPTIONAL)
endforeach()
