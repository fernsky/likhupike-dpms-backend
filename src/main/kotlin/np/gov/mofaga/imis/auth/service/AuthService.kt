package np.gov.mofaga.imis.auth.service

import np.gov.mofaga.imis.auth.api.dto.AuthResponse
import np.gov.mofaga.imis.auth.api.dto.LoginRequest
import np.gov.mofaga.imis.auth.api.dto.RegisterRequest
import np.gov.mofaga.imis.auth.api.dto.RequestPasswordResetRequest
import np.gov.mofaga.imis.auth.api.dto.ResetPasswordRequest

interface AuthService {
    fun register(request: RegisterRequest): AuthResponse

    fun login(request: LoginRequest): AuthResponse

    fun refreshToken(refreshToken: String): AuthResponse

    fun logout(token: String)

    fun requestPasswordReset(request: RequestPasswordResetRequest)

    fun resetPassword(request: ResetPasswordRequest)
}
