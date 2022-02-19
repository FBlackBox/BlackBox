//
// From https://github.com/ganyao114/SandHook/blob/master/hooklib/src/main/cpp/includes/elf_util.h
// Original work Copyright (c) Swift Gan (github user ganyao114)
// Modified work Copyright (c) canyie (github user canyie)
// License: Anti 996 License Version 1.0
// Created by Swift Gan on 2019/3/14.
//

#include "elf_img.h"

#include <malloc.h>
#include <cstring>
#include <sys/mman.h>
#include <unistd.h>
#include "io_wrapper.h"
#include "log.h"
#include "macros.h"
#include "../android.h"

// Pine changed: namespace
using namespace pine;

inline bool CanRead(const char* file) {
    return access(file, R_OK) == 0;
}

void ElfImg::Open(const char* path, bool warn_if_symtab_not_found) {
    //load elf
    int fd = WrappedOpen(path, O_RDONLY | O_CLOEXEC); // Pine changed: add O_CLOEXEC to flags
    if (UNLIKELY(fd == -1)) {
        LOGE("failed to open %s", path);
        return;
    }

    size = lseek(fd, 0, SEEK_END);
    if (UNLIKELY(size <= 0)) {
        LOGE("lseek() failed for %s: errno %d (%s)", path, errno, strerror(errno));
    }

    header = reinterpret_cast<Elf_Ehdr*>(mmap(nullptr, size, PROT_READ, MAP_SHARED, fd, 0));

    close(fd);

    // Pine changed: Use uintptr_t instead of size_t

    // section_header = reinterpret_cast<Elf_Shdr *>(((size_t) header) + header->e_shoff);
    section_header = reinterpret_cast<Elf_Shdr*>(((uintptr_t) header) + header->e_shoff);

    // size_t shoff = reinterpret_cast<size_t>(section_header);
    auto shoff = reinterpret_cast<uintptr_t>(section_header);
    char* section_str = reinterpret_cast<char*>(section_header[header->e_shstrndx].sh_offset +
                                                // ((size_t) header)
                                                ((uintptr_t) header));

    for (int i = 0; i < header->e_shnum; i++, shoff += header->e_shentsize) {
        Elf_Shdr* section_h = (Elf_Shdr*) shoff;
        char* sname = section_h->sh_name + section_str;
        Elf_Off entsize = section_h->sh_entsize;
        switch (section_h->sh_type) {
            case SHT_DYNSYM:
                if (bias == -4396) {
                    dynsym = section_h;
                    dynsym_offset = section_h->sh_offset;
                    dynsym_size = section_h->sh_size;
                    dynsym_count = dynsym_size / entsize;
                    // dynsym_start = reinterpret_cast<Elf_Sym *>(((size_t) header) + dynsym_offset);
                    dynsym_start = reinterpret_cast<Elf_Sym*>(((uintptr_t) header) + dynsym_offset);
                }
                break;
            case SHT_SYMTAB:
                if (strcmp(sname, ".symtab") == 0) {
                    symtab = section_h;
                    symtab_offset = section_h->sh_offset;
                    symtab_size = section_h->sh_size;
                    symtab_count = symtab_size / entsize;
                    // symtab_start = reinterpret_cast<Elf_Sym *>(((size_t) header) + symtab_offset);
                    symtab_start = reinterpret_cast<Elf_Sym*>(((uintptr_t) header) + symtab_offset);
                }
                break;
            case SHT_STRTAB:
                if (bias == -4396) {
                    strtab = section_h;
                    symstr_offset = section_h->sh_offset;
                    // strtab_start = reinterpret_cast<Elf_Sym *>(((size_t) header) + symstr_offset);
                    strtab_start = reinterpret_cast<Elf_Sym*>(((uintptr_t) header) + symstr_offset);
                }
                if (strcmp(sname, ".strtab") == 0) {
                    symstr_offset_for_symtab = section_h->sh_offset;
                }
                break;
            case SHT_PROGBITS:
                if (strtab == nullptr || dynsym == nullptr) break;
                if (bias == -4396) {
                    bias = (off_t) section_h->sh_addr - (off_t) section_h->sh_offset;
                }
                break;
        }
    }

    if (UNLIKELY(!symtab_offset && warn_if_symtab_not_found)) {
        // Pine changed: print log with filename
        // LOGW("can't find symtab from sections\n");
        LOGW("can't find symtab from sections in %s\n", path);
    }

    //load module base
    base = GetModuleBase(path);
}

void ElfImg::RelativeOpen(const char* elf, bool warn_if_symtab_not_found) {
    char buffer[64] = {0}; // We assume that the path length doesn't exceed 64 bytes.
    if (Android::version >= Android::kQ) {
        // Android R: com.android.art
        strcpy(buffer, kApexArtLibDir);
        strcat(buffer, elf);
        if (CanRead(buffer)) {
            Open(buffer, warn_if_symtab_not_found);
            return;
        }

        memset(buffer, 0, sizeof(buffer));

        // Android Q: com.android.runtime
        strcpy(buffer, kApexRuntimeLibDir);
        strcat(buffer, elf);
        if (CanRead(buffer)) {
            Open(buffer, warn_if_symtab_not_found);
            return;
        }

        memset(buffer, 0, sizeof(buffer));
    }
    strcpy(buffer, kSystemLibDir);
    strcat(buffer, elf);
    Open(buffer, warn_if_symtab_not_found);
}


ElfImg::~ElfImg() {
    //open elf file local
    if (buffer) {
        free(buffer);
        buffer = nullptr;
    }
    //use mmap
    if (header) {
        munmap(header, size);
    }
}

Elf_Addr ElfImg::GetSymbolOffset(const char* name, bool warn_if_missing) const {
    Elf_Addr _offset = 0;

    //search dynmtab
    if (dynsym_start != nullptr && strtab_start != nullptr) {
        Elf_Sym* sym = dynsym_start;
        char* strings = (char*) strtab_start;
        int k;
        for (k = 0; k < dynsym_count; k++, sym++)
            if (strcmp(strings + sym->st_name, name) == 0) {
                _offset = sym->st_value;
                // BEGIN Pine changed: Remove log
                // LOGD("find %s: %x\n", elf, _offset);
                // END Pine changed: Remove log
                return _offset;
            }
    }

    //search symtab
    if (symtab_start != nullptr && symstr_offset_for_symtab != 0) {
        for (int i = 0; i < symtab_count; i++) {
            unsigned int st_type = ELF_ST_TYPE(symtab_start[i].st_info);
            // char *st_name = reinterpret_cast<char *>(((size_t) header) + symstr_offset_for_symtab +
            char* st_name = reinterpret_cast<char*>(((uintptr_t) header) +
                                                    symstr_offset_for_symtab +
                                                    symtab_start[i].st_name);
            if (st_type == STT_FUNC && symtab_start[i].st_size) {
                if (strcmp(st_name, name) == 0) {
                    _offset = symtab_start[i].st_value;
                    // BEGIN Pine changed: Remove log
                    // LOGD("find %s: %x\n", elf, _offset);
                    // END Pine changed: Remove log
                    return _offset;
                }
            }
        }
    }
    if (warn_if_missing) LOGE("Symbol %s not found in elf %s", name, elf);
    return 0;
}

void* ElfImg::GetSymbolAddress(const char* name, bool warn_if_missing) const {
    Elf_Addr offset = GetSymbolOffset(name, warn_if_missing);
    if (LIKELY(offset > 0 && base != nullptr)) {
        // Pine changed: Use uintptr_t instead of size_t
        // return reinterpret_cast<void *>((size_t) base + offset - bias);
        return reinterpret_cast<void*>((uintptr_t) base + offset - bias);
    } else {
        return nullptr;
    }
}

void* ElfImg::GetModuleBase(const char* name) {
    FILE* maps;
    char buff[256];
    off_t load_addr;
    // Pine changed: Use bool to instead of int
    bool found = false;
    // Pine changed: add "e" to mode
    maps = WrappedFOpen("/proc/self/maps", "re");
    while (fgets(buff, sizeof(buff), maps)) {
        if (strstr(buff, name) && (strstr(buff, "r-xp") || strstr(buff, "r--p"))) {
            found = true;
            break;
        }
    }

    if (UNLIKELY(!found)) {
        LOGE("failed to read load address for %s", name);
        fclose(maps);
        return nullptr;
    }

    if (UNLIKELY(sscanf(buff, "%lx", &load_addr) != 1))
        LOGE("failed to read load address for %s", name);

    fclose(maps);

    LOGD("get module base %s: %lu", name, load_addr);

    return reinterpret_cast<void*>(load_addr);
}
