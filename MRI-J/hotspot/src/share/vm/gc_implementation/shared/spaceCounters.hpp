/*
 * Copyright 2002-2004 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *  
 */
// This file is a derivative work resulting from (and including) modifications
// made by Azul Systems, Inc.  The date of such changes is 2010.
// Copyright 2010 Azul Systems, Inc.  All Rights Reserved.
//
// Please contact Azul Systems, Inc., 1600 Plymouth Street, Mountain View, 
// CA 94043 USA, or visit www.azulsystems.com if you need additional information 
// or have any questions.
#ifndef SPACECOUNTERS_HPP
#define SPACECOUNTERS_HPP

#include "allocation.hpp"
#include "generationCounters.hpp"
#include "mutableSpace.hpp"
#include "perfData.hpp"
#include "resourceArea.hpp"

#include "allocation.inline.hpp"

// A SpaceCounter is a holder class for performance counters
// that track a space;

class SpaceCounters: public CHeapObj {
 private:
  PerfVariable*      _capacity;
  PerfVariable*      _used;

  // Constant PerfData types don't need to retain a reference.
  // However, it's a good idea to document them here.
  // PerfConstant*     _size;

  MutableSpace*     _object_space;
  char*             _name_space;

 public:

  SpaceCounters(const char* name, int ordinal, size_t max_size,
                MutableSpace* m, GenerationCounters* gc);

  ~SpaceCounters() {
    if (_name_space != NULL) FREE_C_HEAP_ARRAY(char, _name_space);
  }
  
  inline void update_capacity() {
    _capacity->set_value(_object_space->capacity_in_bytes());
  }

  inline void update_used() {
    _used->set_value(_object_space->used_in_bytes());
  }

  inline void update_all() {
    update_used();
    update_capacity();
  }

  const char* name_space() const        { return _name_space; }
};

class MutableSpaceUsedHelper: public PerfLongSampleHelper {
  private:
    MutableSpace* _m;

  public:
    MutableSpaceUsedHelper(MutableSpace* m) : _m(m) { }

    inline jlong take_sample() {
      return _m->used_in_bytes();
    }
};

#endif // SPACECOUNTERS_HPP

