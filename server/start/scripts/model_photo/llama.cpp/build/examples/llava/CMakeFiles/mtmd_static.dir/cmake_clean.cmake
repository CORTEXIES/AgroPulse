file(REMOVE_RECURSE
  "libmtmd_static.a"
  "libmtmd_static.pdb"
)

# Per-language clean rules from dependency scanning.
foreach(lang CXX)
  include(CMakeFiles/mtmd_static.dir/cmake_clean_${lang}.cmake OPTIONAL)
endforeach()
