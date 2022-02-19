//
// Created by canyie on 2021/5/1.
//

#ifndef PINE_GC_DEFS_H
#define PINE_GC_DEFS_H

namespace pine::art {
    class GCCriticalSection {
    private:
        void* self_;
        const char* section_name_;
    };

    enum GcCause {
        // Invalid GC cause used as a placeholder.
        kGcCauseNone,
        // GC triggered by a failed allocation. Thread doing allocation is blocked waiting for GC before
        // retrying allocation.
        kGcCauseForAlloc,
        // A background GC trying to ensure there is free memory ahead of allocations.
        kGcCauseBackground,
        // An explicit System.gc() call.
        kGcCauseExplicit,
        // GC triggered for a native allocation when NativeAllocationGcWatermark is exceeded.
        // (This may be a blocking GC depending on whether we run a non-concurrent collector).
        kGcCauseForNativeAlloc,
        // GC triggered for a collector transition.
        kGcCauseCollectorTransition,
        // Not a real GC cause, used when we disable moving GC (currently for GetPrimitiveArrayCritical).
        kGcCauseDisableMovingGc,
        // Not a real GC cause, used when we trim the heap.
        kGcCauseTrim,
        // Not a real GC cause, used to implement exclusion between GC and instrumentation.
        kGcCauseInstrumentation,
        // Not a real GC cause, used to add or remove app image spaces.
        kGcCauseAddRemoveAppImageSpace,
        // Not a real GC cause, used to implement exclusion between GC and debugger.
        kGcCauseDebugger,
        // GC triggered for background transition when both foreground and background collector are CMS.
        kGcCauseHomogeneousSpaceCompact,
        // Class linker cause, used to guard filling art methods with special values.
        kGcCauseClassLinker,
        // Not a real GC cause, used to implement exclusion between code cache metadata and GC.
        kGcCauseJitCodeCache,
        // Not a real GC cause, used to add or remove system-weak holders.
        kGcCauseAddRemoveSystemWeakHolder,
        // Not a real GC cause, used to prevent hprof running in the middle of GC.
        kGcCauseHprof,
        // Not a real GC cause, used to prevent GetObjectsAllocated running in the middle of GC.
        kGcCauseGetObjectsAllocated,
        // GC cause for the profile saver.
        kGcCauseProfileSaver,
    };

    enum CollectorType {
        // No collector selected.
        kCollectorTypeNone,
        // Non concurrent mark-sweep.
        kCollectorTypeMS,
        // Concurrent mark-sweep.
        kCollectorTypeCMS,
        // Semi-space / mark-sweep hybrid, enables compaction.
        kCollectorTypeSS,
        // Heap trimming collector, doesn't do any actual collecting.
        kCollectorTypeHeapTrim,
        // A (mostly) concurrent copying collector.
        kCollectorTypeCC,
        // The background compaction of the concurrent copying collector.
        kCollectorTypeCCBackground,
        // Instrumentation critical section fake collector.
        kCollectorTypeInstrumentation,
        // Fake collector for adding or removing application image spaces.
        kCollectorTypeAddRemoveAppImageSpace,
        // Fake collector used to implement exclusion between GC and debugger.
        kCollectorTypeDebugger,
        // A homogeneous space compaction collector used in background transition
        // when both foreground and background collector are CMS.
        kCollectorTypeHomogeneousSpaceCompact,
        // Class linker fake collector.
        kCollectorTypeClassLinker,
        // JIT Code cache fake collector.
        kCollectorTypeJitCodeCache,
        // Hprof fake collector.
        kCollectorTypeHprof,
        // Fake collector for installing/removing a system-weak holder.
        kCollectorTypeAddRemoveSystemWeakHolder,
        // Fake collector type for GetObjectsAllocated
        kCollectorTypeGetObjectsAllocated,
        // Fake collector type for ScopedGCCriticalSection
        kCollectorTypeCriticalSection,
    };
}

#endif //PINE_GC_DEFS_H
