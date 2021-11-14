#pragma once

#include <cstdint>
#include <string>
#include <vector>

using int32 = int32_t;
using uint32 = uint32_t;
using int64 = int64_t;
using uint64 = uint64_t;

using Byte = unsigned char;

using PlainText = std::string;
using CipherText = std::vector<Byte>;

/**
 * @brief Convert the data from plaintext to ciphertext.
 * 
 * @param data The plaintext data.
 * @return CipherText The ciphertext.
 */
inline CipherText ToCipherText(const PlainText data) { return CipherText(data.begin(), data.end()); }

/**
 * @brief Convert the data from ciphertext to plaintext.
 * 
 * @param data The ciphertext data.
 * @return PlainText The plaintext data.
 */
inline PlainText ToPlainText(const CipherText data) { return PlainText(data.begin(), data.end()); }
